package com.technote.aquasense.data.repository

import android.content.Context
import com.technote.aquasense.data.model.User
import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.data.remote.api.AuthInterceptor
import com.technote.aquasense.data.remote.dto.LoginRequest
import com.technote.aquasense.data.remote.dto.RegisterRequest
import com.technote.aquasense.data.remote.dto.UpdateUserRequest
import com.technote.aquasense.data.remote.dto.UpdateUserRequestBuilder
import com.technote.aquasense.domain.repository.AuthRepository
import com.technote.aquasense.utils.JwtUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AquasenseApi,
    private val authInterceptor: AuthInterceptor,
    @ApplicationContext private val context: Context
) : AuthRepository{

    private val sharedPrefs by lazy {
        context.getSharedPreferences("aquasense_auth", Context.MODE_PRIVATE)
    }

    // Armazenamento do userId com persistência
    private var _currentUserId: String? = null

    override suspend fun login(email: String, password: String): Result<Pair<String, User>> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = api.login(request)
        if (response.isSuccessful) {
            val apiResponse = response.body()
            if (apiResponse == null) {
                return Result.failure(Exception("Resposta da API está vazia"))
            }
            
            val loginResponse = apiResponse.data
            if (loginResponse == null) {
                return Result.failure(Exception("Dados de login não encontrados na resposta"))
            }
            
            val token = loginResponse.accessToken
            if (token.isNullOrBlank()) {
                return Result.failure(Exception("Token de acesso não foi recebido"))
            }

            // Salva token no interceptor
            authInterceptor.setAuthToken(token)
            
            // Extrai userId do JWT token
            val userId = JwtUtils.extractUserIdFromToken(token)
            if (userId.isNullOrBlank()) {
                return Result.failure(Exception("Não foi possível extrair userId do token"))
            }

            // Salvar userId e email
            saveUserId(userId)
            saveUserEmail(email)

            // Buscar perfil do usuário para obter o nome
            return getUserProfile("").fold(
                onSuccess = { user ->
                    // Criar User com dados do JWT + nome da API
                    val completeUser = User(
                        id = userId,
                        name = user.name,
                        email = email,
                        createdAt = null
                    )
                    Result.success(Pair(token, completeUser))
                },
                onFailure = { error ->
                    // Se falhar ao buscar nome, retorna User só com dados do JWT
                    println("⚠️ Erro ao buscar perfil: ${error.message}")
                    val user = User(
                        id = userId,
                        name = "",
                        email = email,
                        createdAt = null
                    )
                    Result.success(Pair(token, user))
                }
            )
        } else {
            Result.failure(Exception("Email ou senha inválidos. Verifique seus dados e tente novamente."))
        }
        } catch (e: kotlin.Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return registerWithDetails(name, email, password, 25, "Brasil") // Valores padrão
    }

    override suspend fun registerWithDetails(name: String, email: String, password: String, age: Int, country: String): Result<User> {
        return try {
            val request = RegisterRequest(name = name, email = email, age = age, country = country, password = password)
            val response = api.register(request)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val userDto = apiResponse.data
                val user = User(
                    id = userDto?.id ?: "",
                    name = userDto?.name ?: "",
                    email = userDto?.email ?: "",
                    age = userDto?.age,
                    country = userDto?.country,
                    createdAt = userDto?.createdAt ?: ""
                )

                Result.success(user)
            } else {
                Result.failure(Exception("Não foi possível criar sua conta. Verifique os dados informados."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        var lastException: Exception? = null
        
        // Tentar até 3 vezes
        repeat(3) { attempt ->
            try {
                println("🔄 Tentativa ${attempt + 1} - Carregando perfil do usuário via /me")
                val response = api.getCurrentUserProfile()

                if (response.isSuccessful) {
                    val apiResponse = response.body()!!
                    val userDto = apiResponse.data
                    println("✅ Perfil carregado com sucesso: ${userDto?.name}")
                    
                    // A API só retorna 'name', vamos pegar userId e email do armazenamento local
                    val storedUserId = getUserId() ?: ""
                    val storedEmail = getUserEmail() ?: ""
                    
                    val user = User(
                        id = storedUserId,
                        name = userDto?.name ?: "",
                        email = storedEmail, // Email salvo durante o login
                        age = userDto?.age,
                        country = userDto?.country,
                        createdAt = null
                    )

                    return Result.success(user)
                } else {
                    val errorMsg = "Não foi possível carregar os dados do perfil. Tente novamente."
                    println("❌ $errorMsg")
                    lastException = Exception(errorMsg)
                }
            } catch (e: Exception) {
                println("❌ Exceção na tentativa ${attempt + 1} ao carregar perfil: ${e.message}")
                lastException = e
                
                // Aguardar antes da próxima tentativa (exceto na última)
                if (attempt < 2) {
                    try {
                        kotlinx.coroutines.delay(500L * (attempt + 1))
                    } catch (ignored: Exception) {}
                }
            }
        }
        
        println("❌ Todas as tentativas de carregar perfil falharam")
        return Result.failure(lastException ?: Exception("Erro desconhecido ao carregar perfil"))
    }

    override suspend fun updateUserProfile(name: String?, email: String?, age: Int?, country: String?): Result<User> {
        return try {
            println("🔄 Preparando request de atualização de perfil:")
            println("   - Name: $name")
            println("   - Email: $email")
            println("   - Age: $age")
            println("   - Country: $country")
            
            // Obter dados atuais do usuário para comparação
            val currentUser = getUserProfile("").getOrNull()
            println("👤 Usuário atual: $currentUser")
            
            val request = UpdateUserRequestBuilder.buildUpdateRequest(
                currentUser = currentUser,
                newName = name,
                newEmail = email,
                newAge = age,
                newCountry = country
            )
            println("🌐 ${request.getModifiedFields()}")
            
            val response = api.updateUser(request)
            
            println("📡 Resposta da API:")
            println("   - Status Code: ${response.code()}")
            println("   - Status Message: ${response.message()}")
            println("   - Headers: ${response.headers()}")
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                println("✅ Resposta bem-sucedida: $apiResponse")
                
                if (apiResponse?.data == null) {
                    println("⚠️ API retornou dados nulos")
                    return Result.failure(Exception("Dados de resposta da API estão nulos"))
                }
                
                val userDto = apiResponse.data
                
                // Atualizar email salvo localmente se foi alterado
                if (!email.isNullOrBlank()) {
                    saveUserEmail(email)
                }
                
                // Obter dados atuais para manter userId
                val storedUserId = getUserId() ?: ""
                val storedEmail = if (!email.isNullOrBlank()) email else getUserEmail() ?: ""
                
                val user = User(
                    id = storedUserId,
                    name = userDto?.name ?: "",
                    email = storedEmail,
                    createdAt = userDto?.createdAt
                )

                println("✅ Usuário atualizado criado: $user")
                Result.success(user)
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ Erro na resposta da API:")
                println("   - Error Body: $errorBody")
                Result.failure(Exception("Não foi possível atualizar seus dados. Tente novamente mais tarde."))
            }
        } catch (e: Exception) {
            println("❌ Exceção ao atualizar usuário: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateUserPassword(password: String): Result<User> {
        return try {
            println("🔄 Preparando request de atualizacao de senha:")
            println("   - Password: [FORNECIDA]")            
            
            val request = UpdateUserRequestBuilder.buildUpdateRequest(
                currentUser = null, // Para senha não comparamos
                newPassword = password
            )
            println("🌐 ${request.getModifiedFields()}")
            
            val response = api.updateUser(request)
            
            println("📡 Resposta da API:")
            println("   - Status Code: ${response.code()}")
            println("   - Status Message: ${response.message()}")
            println("   - Headers: ${response.headers()}")
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                println("✅ Senha atualizada com sucesso: $apiResponse")
                
                if (apiResponse?.data == null) {
                    println("⚠️ API retornou dados nulos")
                    return Result.failure(Exception("Dados de resposta da API estão nulos"))
                }
                
                val userDto = apiResponse.data
                
                // Obter dados atuais para manter userId e email
                val storedUserId = getUserId() ?: ""
                val storedEmail = getUserEmail() ?: ""
                
                val user = User(
                    id = storedUserId,
                    name = userDto?.name ?: "",
                    email = storedEmail,
                    age = userDto?.age,
                    country = userDto?.country,
                    createdAt = userDto?.createdAt
                )

                println("✅ Usuário com senha atualizada: $user")
                Result.success(user)
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ Erro na resposta da API:")
                println("   - Error Body: $errorBody")
                Result.failure(Exception("Não foi possível alterar sua senha. Tente novamente mais tarde."))
            }
        } catch (e: Exception) {
            println("❌ Exceção ao atualizar senha: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun saveAuthToken(token: String) {
        authInterceptor.setAuthToken(token)
        // TODO: Salvar no DataStore também
        // userPreferences.saveAuthToken(token)
    }
    
    override suspend fun getAuthToken(): String? {
        return authInterceptor.getAuthToken()
        // TODO: Buscar do DataStore
        // return userPreferences.getAuthToken()
    }
    
    override suspend fun saveUserId(userId: String) {
        _currentUserId = userId
        sharedPrefs.edit().putString("user_id", userId).apply()
    }
    
    override suspend fun getUserId(): String? {
        if (_currentUserId == null) {
            _currentUserId = sharedPrefs.getString("user_id", null)
        }
        return _currentUserId
    }

    override suspend fun saveUserEmail(email: String) {
        sharedPrefs.edit().putString("user_email", email).apply()
    }
    
    override suspend fun getUserEmail(): String? {
        return sharedPrefs.getString("user_email", null)
    }

    override suspend fun clearAuthToken() {
        authInterceptor.clearAuthToken()
        authInterceptor.clearTokenExpiredFlag()
        _currentUserId = null
        sharedPrefs.edit()
            .remove("user_id")
            .remove("user_email")
            .apply()
        println("🧹 Dados de autenticação limpos completamente")
    }
    
    override suspend fun isTokenExpired(): Boolean {
        val token = getAuthToken()
        return if (token != null) {
            JwtUtils.isTokenExpired(token)
        } else {
            true
        }
    }
    
    override suspend fun isTokenMarkedExpired(): Boolean {
        return authInterceptor.isTokenMarkedExpired()
    }
    
    override suspend fun clearTokenExpiredFlag() {
        authInterceptor.clearTokenExpiredFlag()
    }

}
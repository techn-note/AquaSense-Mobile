package com.technote.aquasense.di

import com.technote.aquasense.data.remote.api.ApiConstants
import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.data.remote.api.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo Hilt responsável por fornecer dependências relacionadas à rede
 * Configura Retrofit, OkHttpClient, interceptadores e a API interface
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Fornece o cliente HTTP configurado
     * Inclui interceptadores de auth e configurações de timeout
     *
     * @param authInterceptor Interceptador de autenticação
     * @return OkHttpClient configurado
     */

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // adiciona o token automaticamente
            .retryOnConnectionFailure(true)
            .connectTimeout(ApiConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }


    /**
     * Fornece a instância do Retrofit
     * Configurado com URL base, cliente HTTP e conversor JSON
     *
     * @param okHttpClient Cliente HTTP configurado
     * @return Retrofit configurado
     */

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL) // URL base da API
            .client(okHttpClient) // Cliente HTTP com interceptadores
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON -> Kotlin
            .build()
    }

    /**
     * Fornece a interface da API Aquasense
     * Retrofit criará automaticamente a implementação desta interface
     *
     * @param retrofit Instância do Retrofit
     * @return AquasenseApi implementada pelo Retrofit
     */
    @Provides
    @Singleton
    fun provideAquasenseApi(retrofit: Retrofit): AquasenseApi{
        return retrofit.create(AquasenseApi::class.java)
    }

}
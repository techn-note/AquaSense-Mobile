package com.technote.aquasense.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de imagens de perfil local
 * Responsável por salvar, carregar e gerenciar fotos de perfil dos usuários
 */
@Singleton
class ProfileImageManager @Inject constructor() {
    
    companion object {
        private const val PROFILE_IMAGES_DIR = "profile_images"
        // Extensões que aceitamos ao procurar imagens já salvas
        private val IMAGE_EXTENSIONS = arrayOf(".jpg", ".jpeg", ".png")
    }
    
    /**
     * Salva uma imagem de perfil para um usuário específico
     * @param context Contexto da aplicação
     * @param userId ID do usuário
     * @param imageUri URI da imagem selecionada
     * @return URI local da imagem salva, ou null se houver erro
     */
    fun saveProfileImage(context: Context, userId: String, imageUri: Uri): String? {
        return try {
            val profileImagesDir = File(context.filesDir, PROFILE_IMAGES_DIR)
            if (!profileImagesDir.exists()) {
                profileImagesDir.mkdirs()
            }

            // Tentar manter a extensão real do arquivo (png/jpg/jpeg)
            val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
            val ext = when (mimeType.lowercase()) {
                "image/png" -> ".png"
                "image/jpg", "image/jpeg" -> ".jpg"
                else -> ".jpg"
            }

            val imageFile = File(profileImagesDir, "${userId}${ext}")

            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                imageFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Retornar como URI de arquivo (file://) para uso direto com Coil/AsyncImage
            "file://${imageFile.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Obtém a URI da imagem de perfil de um usuário
     * @param context Contexto da aplicação  
     * @param userId ID do usuário
     * @return URI da imagem de perfil, ou null se não existir
     */
    fun getProfileImageUri(context: Context, userId: String): String? {
        return try {
            val profileImagesDir = File(context.filesDir, PROFILE_IMAGES_DIR)
            // Procurar por qualquer extensão suportada
            for (ext in IMAGE_EXTENSIONS) {
                val imageFile = File(profileImagesDir, "${userId}${ext}")
                if (imageFile.exists()) return "file://${imageFile.absolutePath}"
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Remove a imagem de perfil de um usuário
     * @param context Contexto da aplicação
     * @param userId ID do usuário
     * @return true se a imagem foi removida com sucesso
     */
    fun deleteProfileImage(context: Context, userId: String): Boolean {
        return try {
            val profileImagesDir = File(context.filesDir, PROFILE_IMAGES_DIR)
            var allDeleted = true
            for (ext in IMAGE_EXTENSIONS) {
                val imageFile = File(profileImagesDir, "${userId}${ext}")
                if (imageFile.exists()) {
                    val deleted = imageFile.delete()
                    if (!deleted) allDeleted = false
                }
            }
            allDeleted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Cria um URI temporário para captura de foto pela câmera
     * @param context Contexto da aplicação
     * @return URI temporário para captura
     */
    fun createTempImageUri(context: Context): Uri? {
        return try {
            val tempFile = File.createTempFile(
                "profile_image_${UUID.randomUUID()}",
                ".jpg",
                context.cacheDir
            )
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
package com.technote.aquasense.presentation.ui.components.common

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.technote.aquasense.R
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.technote.aquasense.utils.ProfileImageManager

/**
 * Componente para exibir e editar foto de perfil
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImagePicker(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp,
    isEditable: Boolean = true
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    // Launcher para galeria - apenas retorna a URI selecionada (staged)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Não salvar agora; apenas retornar a URI selecionada para staging
            onImageSelected(it.toString())
        }
    }
    
    // Launcher para câmera - apenas retorna a URI temporária (staged)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            onImageSelected(tempCameraUri.toString())
        }
        tempCameraUri = null
    }
    
    // Launcher para permissão de câmera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val profileImageManager = ProfileImageManager()
            tempCameraUri = profileImageManager.createTempImageUri(context)
            tempCameraUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFE3F2FD))
            .border(4.dp, Color(0xFF2196F3), CircleShape)
            .then(
                if (isEditable) {
                    Modifier.clickable { showBottomSheet = true }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (currentImageUri != null && currentImageUri.isNotBlank()) {
            // Exibir imagem de perfil
            AsyncImage(
                model = Uri.parse(currentImageUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Exibir ícone padrão
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar padrão",
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF2196F3)
            )
        }
        
        // Indicador de edição
        if (isEditable) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2196F3))
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Editar foto",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
    
    // Bottom Sheet para seleção de imagem
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.choose_profile_photo),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Opção: Galeria
                ListItem(
                    headlineContent = { 
                        Text(stringResource(R.string.gallery_option), color = Color(0xFF1A1A1A)) 
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.PhotoLibrary, 
                            contentDescription = null,
                            tint = Color(0xFF2196F3)
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        galleryLauncher.launch("image/*")
                    }
                )
                
                // Opção: Câmera
                ListItem(
                    headlineContent = { 
                        Text(stringResource(R.string.camera_option), color = Color(0xFF1A1A1A)) 
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.CameraAlt, 
                            contentDescription = null,
                            tint = Color(0xFF2196F3)
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        // Verificar permissão de câmera
                        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                            PackageManager.PERMISSION_GRANTED -> {
                                val profileImageManager = ProfileImageManager()
                                tempCameraUri = profileImageManager.createTempImageUri(context)
                                tempCameraUri?.let { uri ->
                                    cameraLauncher.launch(uri)
                                }
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }
                )
                
                // Opção: Remover foto (se há uma foto atual)
                if (!currentImageUri.isNullOrBlank()) {
                    ListItem(
                        headlineContent = { 
                            Text(stringResource(R.string.remove_photo_option), color = Color.Red) 
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.Person, 
                                contentDescription = null,
                                tint = Color.Red
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier.clickable {
                            showBottomSheet = false
                            // sinaliza remoção com null
                            onImageSelected(null)
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
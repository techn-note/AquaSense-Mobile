package com.technote.aquasense.utils

object Constants {
    // API - Para testes locais use: "http://10.0.2.2:5000/" (Android Emulator)
    // Para dispositivo físico use o IP da sua máquina local: "http://192.168.x.x:5000/"
    // Para servidor remoto: use o IP atual
    const val BASE_URL = "http://18.226.251.146:5000/"
    const val TIMEOUT_SECONDS = 30L

    // Sensor Parameters
    object SensorLimits {
        const val TEMP_MIN = 18.0
        const val TEMP_MAX = 28.0
        const val PH_MIN = 6.8
        const val PH_MAX = 7.2
        const val VOLUME_MIN = 25.0
        const val VOLUME_MAX = 30.0
        const val TDS_MIN = 200
        const val TDS_MAX = 400
    }

    // Storage Keys
    object PrefsKeys {
        const val ACCESS_TOKEN = "access_token"
        const val USER_NAME = "user_name"
        const val SELECTED_TANKS = "selected_tank"
    }
    // Error Messages
    object Errors {
        const val NETWORK_ERROR = "Erro de conexão. Verifique sua internet."
        const val INVALID_CREDENTIALS = "Credenciais inválidas."
        const val UNKNOWN_ERROR = "Erro desconhecido."
    }
}
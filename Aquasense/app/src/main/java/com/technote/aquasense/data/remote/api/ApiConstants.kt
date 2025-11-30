package com.technote.aquasense.data.remote.api

import com.technote.aquasense.utils.Constants

object ApiConstants {

    // URLs Base
    const val BASE_URL = Constants.BASE_URL
    const val API_VERSION = "v1"

    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_BEARER = "Bearer"
    const val HEADER_ACCEPT = "Accept"

    // Content Types
    const val CONTENT_TYPE_JSON = "application/json"

    // Timeouts (em segundos)
    const val CONNECT_TIMEOUT = Constants.TIMEOUT_SECONDS
    const val READ_TIMEOUT = Constants.TIMEOUT_SECONDS
    const val WRITE_TIMEOUT = Constants.TIMEOUT_SECONDS

    // Endpoints - Autenticação
    const val ENDPOINT_LOGIN = "entrar"
    const val ENDPOINT_REGISTER = "registrar"
    const val ENDPOINT_USER_PROFILE = "perfil"
    const val ENDPOINT_UPDATE_USER = "alteraruser"

    // Endpoints - Tanques
    const val ENDPOINT_TANKS = "tanque"
    const val ENDPOINT_TANK_BY_ID = "tanks/{tankId}"
    const val ENDPOINT_TANK_BY_NAME = "tanque/nome/{tankName}"

    // Endpoints - Peixes
    const val ENDPOINT_PEIXES = "peixes"
    const val ENDPOINT_PEIXE_BY_ID = "peixes/{peixeId}"
    const val ENDPOINT_PEIXE_BY_NAME = "peixes/nome/{peixeName}"

    // Endpoints - Sensores
    const val ENDPOINT_SENSORES = "sensores"
    const val ENDPOINT_SENSORES_LATEST = "sensores/ultima"
    const val ENDPOINT_SENSORES_HISTORY = "sensores/ultima/10"
    const val ENDPOINT_SENSORES_ALL = "sensores/todas"

    // Endpoints - Predições
    const val ENDPOINT_PREDICT = "predict"
    const val ENDPOINT_NEURAL_PREDICT = "neural-predict"

    // Status Codes
    const val STATUS_CODE_SUCCESS = 200
    const val STATUS_CODE_CREATED = 201
    const val STATUS_CODE_BAD_REQUEST = 400
    const val STATUS_CODE_UNAUTHORIZED = 401
    const val STATUS_CODE_FORBIDDEN = 403
    const val STATUS_CODE_NOT_FOUND = 404
    const val STATUS_CODE_INTERNAL_SERVER_ERROR = 500

}
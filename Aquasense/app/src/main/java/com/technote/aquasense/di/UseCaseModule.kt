package com.technote.aquasense.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Módulo Hilt para injeção de dependêncoas dps Use Cases
 * Os Use Cases já tem @Inject em seus construtores
 * mas este módulo pode ser usado para configurações especiais
 */

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // Os Use Cases serão automaticamente injetados por terem @Inject nos construtores
    // Este modulo é apenas para:
    // - Configurações especiais de escopo
    // - Providers customizados
    // - Testes Específicos

    // Exemplo de provider customizado (se necessário):
    /*
    @Provides
    @Singleton
    fun provideCustomLoginUseCase(
        authRepository: AuthRepository,
        // outros deps especiais
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }
    */
}
package com.technote.aquasense.di

import com.technote.aquasense.data.repository.AIRepositoryImpl
import com.technote.aquasense.data.repository.AuthRepositoryImpl
import com.technote.aquasense.data.repository.TankRepositoryImpl
import com.technote.aquasense.data.repository.FishRepositoryImpl
import com.technote.aquasense.data.repository.SensorRepositoryImpl
import com.technote.aquasense.data.repository.PredictionRepositoryImpl
import com.technote.aquasense.domain.repository.AIRepository
import com.technote.aquasense.domain.repository.AuthRepository
import com.technote.aquasense.domain.repository.FishRepository
import com.technote.aquasense.domain.repository.PredictionRepository
import com.technote.aquasense.domain.repository.SensorRepository
import com.technote.aquasense.domain.repository.TankRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTankRepository(
        tankRepositoryImpl: TankRepositoryImpl
    ): TankRepository

    @Binds
    @Singleton
    abstract fun bindFishRepository(
        fishRepositoryImpl: FishRepositoryImpl
    ): FishRepository

    @Binds
    @Singleton
    abstract fun bindSensorRepository(
        sensorRepositoryImpl: SensorRepositoryImpl
    ): SensorRepository

    @Binds
    @Singleton
    abstract fun bindPredictionRepository(
        predictionRepositoryImpl: PredictionRepositoryImpl
    ): PredictionRepository

    @Binds
    @Singleton
    abstract fun bindAIRepository(
        aiRepositoryImpl: AIRepositoryImpl
    ): AIRepository

}
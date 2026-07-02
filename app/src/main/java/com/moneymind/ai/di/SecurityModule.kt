package com.moneymind.ai.di

import com.moneymind.ai.core.security.AppLockManager
import com.moneymind.ai.core.security.BiometricAuthManager
import com.moneymind.ai.core.security.SecureStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    fun provideAppLockManager(secureStore: SecureStore): AppLockManager =
        AppLockManager(secureStore)

    @Provides
    fun provideBiometricAuthManager(): BiometricAuthManager = BiometricAuthManager()
}

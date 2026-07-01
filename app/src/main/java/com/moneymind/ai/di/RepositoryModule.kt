package com.moneymind.ai.di

import com.moneymind.ai.data.repository.LoanRepositoryImpl
import com.moneymind.ai.data.repository.SubscriptionRepositoryImpl
import com.moneymind.ai.data.repository.TransactionRepositoryImpl
import com.moneymind.ai.domain.repository.LoanRepository
import com.moneymind.ai.domain.repository.SubscriptionRepository
import com.moneymind.ai.domain.repository.TransactionRepository
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
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindLoanRepository(
        impl: LoanRepositoryImpl
    ): LoanRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        impl: SubscriptionRepositoryImpl
    ): SubscriptionRepository
}

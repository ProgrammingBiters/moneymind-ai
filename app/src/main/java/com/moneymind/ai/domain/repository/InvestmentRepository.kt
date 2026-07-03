package com.moneymind.ai.domain.repository

import com.moneymind.ai.data.local.entity.InvestmentEntity

interface InvestmentRepository {
    suspend fun addInvestment(investment: InvestmentEntity): Long
    suspend fun updateInvestment(investment: InvestmentEntity)
    suspend fun deleteInvestment(investment: InvestmentEntity)
    suspend fun getAllActive(): List<InvestmentEntity>
    suspend fun getTotalInvested(): Double
    suspend fun getTotalCurrentValue(): Double
}

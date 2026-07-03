package com.moneymind.ai.data.repository

import com.moneymind.ai.data.local.dao.InvestmentDao
import com.moneymind.ai.data.local.entity.InvestmentEntity
import com.moneymind.ai.domain.repository.InvestmentRepository
import javax.inject.Inject

class InvestmentRepositoryImpl @Inject constructor(
    private val dao: InvestmentDao
) : InvestmentRepository {
    override suspend fun addInvestment(investment: InvestmentEntity): Long = dao.insert(investment)
    override suspend fun updateInvestment(investment: InvestmentEntity) = dao.update(investment)
    override suspend fun deleteInvestment(investment: InvestmentEntity) = dao.delete(investment)
    override suspend fun getAllActive(): List<InvestmentEntity> = dao.getAllActive()
    override suspend fun getTotalInvested(): Double = dao.getTotalInvested()
    override suspend fun getTotalCurrentValue(): Double = dao.getTotalCurrentValue()
}

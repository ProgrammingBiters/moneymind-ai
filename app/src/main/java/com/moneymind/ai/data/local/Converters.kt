package com.moneymind.ai.data.local

import androidx.room.TypeConverter
import com.moneymind.ai.domain.model.BillingCycle
import com.moneymind.ai.domain.model.InvestmentType
import com.moneymind.ai.domain.model.Ledger
import com.moneymind.ai.domain.model.LiabilityType
import com.moneymind.ai.domain.model.LoanDirection
import com.moneymind.ai.domain.model.PaymentMode
import com.moneymind.ai.domain.model.TransactionCategory
import com.moneymind.ai.domain.model.TransactionType

/** Enums are stored as their [Enum.name] string — stable, readable in raw SQL, and cheap to compare. */
class Converters {

    @TypeConverter
    fun ledgerToString(value: Ledger): String = value.name

    @TypeConverter
    fun stringToLedger(value: String): Ledger = Ledger.valueOf(value)

    @TypeConverter
    fun typeToString(value: TransactionType): String = value.name

    @TypeConverter
    fun stringToType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun categoryToString(value: TransactionCategory): String = value.name

    @TypeConverter
    fun stringToCategory(value: String): TransactionCategory = TransactionCategory.valueOf(value)

    @TypeConverter
    fun paymentModeToString(value: PaymentMode): String = value.name

    @TypeConverter
    fun stringToPaymentMode(value: String): PaymentMode = PaymentMode.valueOf(value)

    @TypeConverter
    fun loanDirectionToString(value: LoanDirection): String = value.name

    @TypeConverter
    fun stringToLoanDirection(value: String): LoanDirection = LoanDirection.valueOf(value)

    @TypeConverter
    fun billingCycleToString(value: BillingCycle): String = value.name

    @TypeConverter
    fun stringToBillingCycle(value: String): BillingCycle = BillingCycle.valueOf(value)

    @TypeConverter
    fun investmentTypeToString(value: InvestmentType): String = value.name

    @TypeConverter
    fun stringToInvestmentType(value: String): InvestmentType = InvestmentType.valueOf(value)

    @TypeConverter
    fun liabilityTypeToString(value: LiabilityType): String = value.name

    @TypeConverter
    fun stringToLiabilityType(value: String): LiabilityType = LiabilityType.valueOf(value)
}

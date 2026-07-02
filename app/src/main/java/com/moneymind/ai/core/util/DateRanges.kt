package com.moneymind.ai.core.util

import java.time.LocalDate
import java.time.ZoneId

/** Epoch-millis [start, end] windows used for "today" / "this month" dashboard sums. */
object DateRanges {

    fun today(): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val startOfDay = LocalDate.now(zone).atStartOfDay(zone).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()
        return startOfDay to now
    }

    fun thisMonth(): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        val startOfMonth = LocalDate.now(zone).withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()
        return startOfMonth to now
    }

    /** Full history window, for ledger balance sums that shouldn't be date-bounded. */
    fun allTime(): Pair<Long, Long> = 0L to Long.MAX_VALUE
}

package com.moneymind.ai.core.sms

import android.content.Context
import android.provider.Telephony
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class RawSms(
    val sender: String,
    val body: String,
    val dateMillis: Long
)

/**
 * Reads the device's SMS inbox via ContentResolver. Requires READ_SMS,
 * requested at runtime by the SMS Import screen — nothing here reads SMS
 * without that grant, and nothing here ever leaves the device; parsing
 * happens entirely on-device in [SmsTransactionParser].
 */
@Singleton
class SmsReader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun readRecentInbox(maxAgeMillis: Long = 90L * 24 * 60 * 60 * 1000, limit: Int = 300): List<RawSms> {
        val results = mutableListOf<RawSms>()
        val cutoff = System.currentTimeMillis() - maxAgeMillis

        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE
        )

        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            projection,
            "${Telephony.Sms.DATE} > ?",
            arrayOf(cutoff.toString()),
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

            while (it.moveToNext() && results.size < limit) {
                val address = if (addressIndex >= 0) it.getString(addressIndex) else null
                val body = if (bodyIndex >= 0) it.getString(bodyIndex) else null
                val date = if (dateIndex >= 0) it.getLong(dateIndex) else System.currentTimeMillis()

                if (!address.isNullOrBlank() && !body.isNullOrBlank()) {
                    results.add(RawSms(sender = address, body = body, dateMillis = date))
                }
            }
        }

        return results
    }
}

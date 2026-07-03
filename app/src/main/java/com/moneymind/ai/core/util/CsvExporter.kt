package com.moneymind.ai.core.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.moneymind.ai.data.local.entity.TransactionEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Writes transactions to a CSV file under the app's cache dir and returns a
 * content:// URI (via FileProvider) suitable for sharing through Android's
 * share sheet. Nothing here touches the network — the file only ever leaves
 * the device if the user explicitly shares it from that sheet.
 */
object CsvExporter {

    fun export(context: Context, transactions: List<TransactionEntity>, fileName: String): Uri {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, fileName)

        file.bufferedWriter().use { writer ->
            writer.write("Date,Type,Ledger,Category,Amount,Payment Mode,Merchant,Note\n")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            transactions.forEach { tx ->
                writer.write(
                    listOf(
                        dateFormat.format(Date(tx.dateMillis)),
                        tx.type.name,
                        tx.ledger.displayName,
                        tx.category.displayName,
                        tx.amount.toString(),
                        tx.paymentMode.displayName,
                        escape(tx.merchant ?: ""),
                        escape(tx.note ?: "")
                    ).joinToString(",") + "\n"
                )
            }
        }

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun escape(value: String): String =
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
}

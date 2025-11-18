package cl.duoc.levelupgamer.util

import java.text.NumberFormat
import java.util.Locale

private val currencyFormatter = object : ThreadLocal<NumberFormat>() {
    override fun initialValue(): NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }
}

fun formatCurrency(amount: Double): String = currencyFormatter.get().format(amount)

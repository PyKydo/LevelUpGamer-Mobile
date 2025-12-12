package cl.duoc.levelupgamer.util

object ChileanIdValidator {
    fun isValid(rut: String): Boolean {
        val cleanRut = rut.replace(".", "").replace("-", "").uppercase()
        if (cleanRut.length < 2) return false

        val body = cleanRut.dropLast(1)
        val dv = cleanRut.last()

        if (!body.all { it.isDigit() }) return false

        var sum = 0
        var multiplier = 2
        for (digit in body.reversed()) {
            sum += Character.getNumericValue(digit) * multiplier
            multiplier++
            if (multiplier == 8) multiplier = 2
        }

        val remainder = 11 - (sum % 11)
        val calculatedDv = when (remainder) {
            11 -> '0'
            10 -> 'K'
            else -> Character.forDigit(remainder, 10)
        }

        return dv == calculatedDv
    }
}

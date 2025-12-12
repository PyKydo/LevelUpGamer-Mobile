package cl.duoc.levelupgamer.model.local

import androidx.room.TypeConverter

class RoomConverters {
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun toStringList(values: List<String>?): String {
        if (values.isNullOrEmpty()) return ""
        return values.joinToString(separator = "|") { it.trim() }
    }
}

package jp.cordea.closet.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date = Date(value)

    @TypeConverter
    fun fromDate(value: Date): Long = value.time

    @TypeConverter
    fun fromTypeString(value: String): ItemType = ItemType.valueOf(value)

    @TypeConverter
    fun fromType(value: ItemType): String = value.name

    @TypeConverter
    fun fromListJson(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromList(value: List<String>): String = Json.encodeToString(value)
}

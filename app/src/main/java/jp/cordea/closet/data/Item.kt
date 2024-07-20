package jp.cordea.closet.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.Date

private val formatter = NumberFormat.getInstance()

@Entity
data class Item(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: ItemType,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "updated_at") val updatedAt: Date,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "material") val material: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "size") val size: String,
    @ColumnInfo(name = "bust") val bust: Float,
    @ColumnInfo(name = "length") val length: Float,
    @ColumnInfo(name = "height") val height: Float,
    @ColumnInfo(name = "width") val width: Float,
    @ColumnInfo(name = "depth") val depth: Float,
    @ColumnInfo(name = "waist") val waist: Float,
    @ColumnInfo(name = "hip") val hip: Float,
    @ColumnInfo(name = "sleeve_length") val sleeveLength: Float,
    @ColumnInfo(name = "shoulder_width") val shoulderWidth: Float,
    @ColumnInfo(name = "neck_size") val neckSize: Float,
    @ColumnInfo(name = "inseam") val inseam: Float,
    @ColumnInfo(name = "rise") val rise: Float,
    @ColumnInfo(name = "leg_opening") val legOpening: Float,
    @ColumnInfo(name = "knee") val knee: Float,
    @ColumnInfo(name = "thigh") val thigh: Float,
    @ColumnInfo(name = "head_circumference") val headCircumference: Float,
    @ColumnInfo(name = "tags") val tags: List<String>
) {
    fun asMap(): Map<ItemAttribute, String> {
        return mapOf(
            ItemAttribute.TITLE to title,
            ItemAttribute.DESCRIPTION to description,
            ItemAttribute.MATERIAL to material,
            ItemAttribute.SIZE to size,
            ItemAttribute.BUST to formatter.format(bust),
            ItemAttribute.LENGTH to formatter.format(length),
            ItemAttribute.HEIGHT to formatter.format(height),
            ItemAttribute.WIDTH to formatter.format(width),
            ItemAttribute.DEPTH to formatter.format(depth),
            ItemAttribute.WAIST to formatter.format(waist),
            ItemAttribute.HIP to formatter.format(hip),
            ItemAttribute.SLEEVE_LENGTH to formatter.format(sleeveLength),
            ItemAttribute.SHOULDER_WIDTH to formatter.format(shoulderWidth),
            ItemAttribute.NECK_SIZE to formatter.format(neckSize),
            ItemAttribute.INSEAM to formatter.format(inseam),
            ItemAttribute.RISE to formatter.format(rise),
            ItemAttribute.LEG_OPENING to formatter.format(legOpening),
            ItemAttribute.KNEE to formatter.format(knee),
            ItemAttribute.THIGH to formatter.format(thigh),
            ItemAttribute.HEAD_CIRCUMFERENCE to formatter.format(headCircumference),
        )
    }
}

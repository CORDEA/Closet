package jp.cordea.closet.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import jp.cordea.closet.R
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemAttribute.BUST
import jp.cordea.closet.data.ItemAttribute.DEPTH
import jp.cordea.closet.data.ItemAttribute.HEAD_CIRCUMFERENCE
import jp.cordea.closet.data.ItemAttribute.HEIGHT
import jp.cordea.closet.data.ItemAttribute.HIP
import jp.cordea.closet.data.ItemAttribute.INSEAM
import jp.cordea.closet.data.ItemAttribute.KNEE
import jp.cordea.closet.data.ItemAttribute.LEG_OPENING
import jp.cordea.closet.data.ItemAttribute.LENGTH
import jp.cordea.closet.data.ItemAttribute.MATERIAL
import jp.cordea.closet.data.ItemAttribute.NECK_SIZE
import jp.cordea.closet.data.ItemAttribute.RISE
import jp.cordea.closet.data.ItemAttribute.SHOULDER_WIDTH
import jp.cordea.closet.data.ItemAttribute.SIZE
import jp.cordea.closet.data.ItemAttribute.SLEEVE_LENGTH
import jp.cordea.closet.data.ItemAttribute.TAG
import jp.cordea.closet.data.ItemAttribute.THIGH
import jp.cordea.closet.data.ItemAttribute.WAIST
import jp.cordea.closet.data.ItemAttribute.WIDTH

@Composable
fun ItemAttribute.toLocalizedString(): String {
    return when (this) {
        MATERIAL -> stringResource(R.string.attribute_material)
        SIZE -> stringResource(R.string.attribute_size)
        BUST -> stringResource(R.string.attribute_bust)
        LENGTH -> stringResource(R.string.attribute_length)
        HEIGHT -> stringResource(R.string.attribute_height)
        WIDTH -> stringResource(R.string.attribute_width)
        DEPTH -> stringResource(R.string.attribute_depth)
        WAIST -> stringResource(R.string.attribute_waist)
        HIP -> stringResource(R.string.attribute_hip)
        SLEEVE_LENGTH -> stringResource(R.string.attribute_sleeve_length)
        SHOULDER_WIDTH -> stringResource(R.string.attribute_shoulder_width)
        NECK_SIZE -> stringResource(R.string.attribute_neck_size)
        INSEAM -> stringResource(R.string.attribute_inseam)
        RISE -> stringResource(R.string.attribute_rise)
        LEG_OPENING -> stringResource(R.string.attribute_leg_opening)
        KNEE -> stringResource(R.string.attribute_knee)
        THIGH -> stringResource(R.string.attribute_thigh)
        HEAD_CIRCUMFERENCE -> stringResource(R.string.attribute_head_circumference)
        TAG -> stringResource(R.string.attribute_tag)
    }
}

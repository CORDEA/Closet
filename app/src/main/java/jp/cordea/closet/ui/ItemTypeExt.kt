package jp.cordea.closet.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Girl
import androidx.compose.material.icons.filled.IceSkating
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import jp.cordea.closet.R
import jp.cordea.closet.data.ItemType

@Composable
fun ItemType.toLocalizedString(): String {
    return when (this) {
        ItemType.TOPS -> stringResource(R.string.type_tops)
        ItemType.OUTERWEAR -> stringResource(R.string.type_outerwear)
        ItemType.PANTS -> stringResource(R.string.type_pants)
        ItemType.SKIRTS -> stringResource(R.string.type_skirts)
        ItemType.DRESSES -> stringResource(R.string.type_dresses)
        ItemType.SHOES -> stringResource(R.string.type_shoes)
        ItemType.BAGS -> stringResource(R.string.type_bags)
        ItemType.HATS -> stringResource(R.string.type_hats)
        ItemType.RINGS -> stringResource(R.string.type_rings)
        ItemType.OTHERS -> stringResource(R.string.type_others)
    }
}

@Composable
fun ItemType.toIconResource(): ImageVector {
    return when (this) {
        ItemType.TOPS -> Icons.Default.Checkroom
        ItemType.OUTERWEAR -> Icons.Default.AcUnit
        ItemType.PANTS -> Icons.Default.Accessibility
        ItemType.SKIRTS -> Icons.Default.Girl
        ItemType.DRESSES -> Icons.Default.Nightlight
        ItemType.SHOES -> Icons.Default.IceSkating
        ItemType.BAGS -> Icons.Default.ShoppingBag
        ItemType.HATS -> Icons.Default.SmartToy
        ItemType.RINGS -> Icons.Default.Circle
        ItemType.OTHERS -> Icons.Default.Category
    }
}

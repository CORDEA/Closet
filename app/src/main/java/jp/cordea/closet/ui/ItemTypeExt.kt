package jp.cordea.closet.ui

import androidx.compose.runtime.Composable
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
        ItemType.SWIMWEAR -> stringResource(R.string.type_swimwear)
        ItemType.SHOES -> stringResource(R.string.type_shoes)
        ItemType.BAGS -> stringResource(R.string.type_bags)
        ItemType.HATS -> stringResource(R.string.type_hats)
        ItemType.RINGS -> stringResource(R.string.type_rings)
        ItemType.OTHERS -> stringResource(R.string.type_others)
    }
}

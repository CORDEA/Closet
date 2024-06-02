package jp.cordea.closet.ui.add_item

import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType

data class AddItemUiState(
    val type: ItemType = ItemType.TOPS,
    val values: Map<ItemAttribute, String> = emptyMap(),
    val tags: List<String> = emptyList()
)

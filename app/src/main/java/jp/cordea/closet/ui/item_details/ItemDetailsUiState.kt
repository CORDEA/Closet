package jp.cordea.closet.ui.item_details

import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import java.util.Date

data class ItemDetailsUiState(
    val id: String = "",
    val type: ItemType = ItemType.TOPS,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val imagePath: String = "",
    val values: Map<ItemAttribute, String> = emptyMap(),
    val tags: List<String> = emptyList(),
    val isEditOpen: Boolean = false
)

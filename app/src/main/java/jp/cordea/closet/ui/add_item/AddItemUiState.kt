package jp.cordea.closet.ui.add_item

import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType

sealed class AddItemUiState {
    data object Loading : AddItemUiState()

    data class Loaded(
        val type: ItemType = ItemType.TOPS,
        val imagePath: String = "",
        val values: Map<ItemAttribute, String> = emptyMap(),
        val tags: List<String> = emptyList(),
        val hasTitleError: Boolean = false,
        val isHomeOpen: Boolean = false
    ) : AddItemUiState()

    data object Failed : AddItemUiState()

    val canAdd: Boolean
        get() = when (this) {
            Failed -> false
            is Loaded -> true
            Loading -> false
        }
}

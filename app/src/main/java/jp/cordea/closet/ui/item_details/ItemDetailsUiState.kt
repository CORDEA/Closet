package jp.cordea.closet.ui.item_details

import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import java.util.Date

sealed class ItemDetailsUiState {
    data object Loading : ItemDetailsUiState()

    data class Loaded(
        val id: String = "",
        val type: ItemType = ItemType.TOPS,
        val createdAt: Date = Date(),
        val updatedAt: Date = Date(),
        val imagePath: String = "",
        val values: Map<ItemAttribute, String> = emptyMap(),
        val tags: List<String> = emptyList(),
        val isEditOpen: Boolean = false,
        val isHomeOpen: Boolean = false,
        val isDeleteDialogOpen: Boolean = false,
        val hasDeletingError: Boolean = false
    ) : ItemDetailsUiState() {

        val showThumbnail: Boolean get() = imagePath.isNotBlank()

        val showDescription: Boolean
            get() = values.getOrDefault(ItemAttribute.DESCRIPTION, "").isNotBlank()
    }

    data object Failed : ItemDetailsUiState()

    val title: String
        get() = when (this) {
            Failed -> "-"
            is Loaded -> values.getOrDefault(ItemAttribute.TITLE, "")
            Loading -> "-"
        }

    val canEdit: Boolean
        get() = when (this) {
            Failed -> false
            is Loaded -> true
            Loading -> false
        }
}

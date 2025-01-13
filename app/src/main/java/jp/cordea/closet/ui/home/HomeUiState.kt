package jp.cordea.closet.ui.home

import jp.cordea.closet.data.ItemType

data class HomeUiState(
    val state: LoadingState,
    val items: List<HomeItem> = emptyList(),
    val searchResult: List<HomeItem> = emptyList(),
    val isSearchExpanded: Boolean = false,
    val searchQuery: String = "",
    val tags: Map<String, Boolean> = emptyMap(),
    val types: Map<ItemType, Boolean> = emptyMap(),
    val showTypes: Boolean = false,
    val showTags: Boolean = false
) {
    val tagsEnabled get() = tags.isNotEmpty()
    val typesEnabled get() = types.isNotEmpty()
}

data class HomeItem(
    val id: String,
    val title: String,
    val imagePath: String,
    val tags: List<String>
)

enum class LoadingState {
    LOADING,
    LOADED,
    FAILED
}

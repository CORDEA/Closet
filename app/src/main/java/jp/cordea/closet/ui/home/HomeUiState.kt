package jp.cordea.closet.ui.home

import jp.cordea.closet.data.ItemType

sealed class HomeUiState {
    data object Loading : HomeUiState()

    data class Loaded(
        val items: List<HomeItem> = emptyList(),
        val searchResult: List<HomeItem> = emptyList(),
        val isSearchExpanded: Boolean = false,
        val searchQuery: String = "",
        val tags: Map<String, Boolean> = emptyMap(),
        val types: Map<ItemType, Boolean> = emptyMap(),
    ) : HomeUiState()

    data object Failed : HomeUiState()
}

data class HomeItem(
    val id: String,
    val title: String,
    val imagePath: String,
    val tags: List<String>
)

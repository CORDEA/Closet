package jp.cordea.closet.ui.home

sealed class HomeUiState {
    data object Loading : HomeUiState()

    data class Loaded(
        val items: List<HomeItem> = emptyList()
    ) : HomeUiState()

    data object Failed : HomeUiState()
}

data class HomeItem(
    val id: String,
    val title: String,
    val imagePath: String,
    val tags: List<String>
)

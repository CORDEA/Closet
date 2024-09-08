package jp.cordea.closet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {
    private val _state: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val state get() = _state.asStateFlow()

    init {
        fetch()
    }

    fun onAdded() {
        fetch()
    }

    fun onSearchExpanded(expanded: Boolean) {
        val state = _state.value
        if (state !is HomeUiState.Loaded) {
            return
        }
        _state.value = state.copy(isSearchExpanded = expanded)
    }

    fun onSearchQueryChanged(query: String) {
        val state = _state.value
        if (state !is HomeUiState.Loaded) {
            return
        }
        _state.value = state.copy(searchQuery = query)
    }

    private fun fetch() {
        viewModelScope.launch {
            runCatching {
                _state.value = HomeUiState.Loaded(
                    items = repository.findAll()
                        .map { HomeItem(it.id, it.title, it.imagePath, it.tags) }
                )
            }.onFailure {
                _state.value = HomeUiState.Failed
            }
        }
    }

    fun onReload() {
        fetch()
    }
}

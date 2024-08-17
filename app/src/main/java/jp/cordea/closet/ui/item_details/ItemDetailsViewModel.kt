package jp.cordea.closet.ui.item_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ItemRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ItemDetailsUiState>(ItemDetailsUiState.Loading)
    val state get() = _state.asStateFlow()

    private val id = requireNotNull(savedStateHandle.get<String>("id"))

    init {
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            val item = repository.find(id)
            _state.value = ItemDetailsUiState.Loaded(
                id = item.id,
                type = item.type,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt,
                imagePath = item.imagePath,
                values = item.asMap(),
                tags = item.tags
            )
        }
    }

    fun onEditClicked() {
        val state = _state.value
        if (state !is ItemDetailsUiState.Loaded || state.id.isBlank()) {
            return
        }
        _state.value = state.copy(isEditOpen = true)
    }

    fun onEditOpened() {
        val state = _state.value
        require(state is ItemDetailsUiState.Loaded)
        _state.value = state.copy(isEditOpen = false)
    }

    fun onReload() {
        fetch()
    }
}

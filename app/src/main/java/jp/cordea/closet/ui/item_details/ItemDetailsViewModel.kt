package jp.cordea.closet.ui.item_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.repository.ItemRepository
import jp.cordea.closet.repository.ThumbnailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val thumbnailRepository: ThumbnailRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ItemDetailsUiState>(ItemDetailsUiState.Loading)
    val state get() = _state.asStateFlow()

    private val id = requireNotNull(savedStateHandle.get<String>("id"))

    init {
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            val item = itemRepository.find(id)
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
        if (state !is ItemDetailsUiState.Loaded) {
            return
        }
        _state.value = state.copy(isEditOpen = true)
    }

    fun onEditOpened() {
        val state = _state.value
        require(state is ItemDetailsUiState.Loaded)
        _state.value = state.copy(isEditOpen = false)
    }

    fun onDeleteClicked() {
        val state = _state.value
        if (state !is ItemDetailsUiState.Loaded) {
            return
        }
        _state.value = state.copy(isDeleteDialogOpen = true)
    }

    fun onDeleteDialogConfirmed() {
        val state = _state.value
        require(state is ItemDetailsUiState.Loaded)
        viewModelScope.launch {
            runCatching {
                itemRepository.delete(state.id)
            }.onFailure {
                _state.value = state.copy(
                    isDeleteDialogOpen = false,
                    hasDeletingError = true
                )
                return@launch
            }
            runCatching {
                thumbnailRepository.delete(state.imagePath)
            }.onFailure {
                // TODO
            }
            _state.value = state.copy(
                isDeleteDialogOpen = false,
                isHomeOpen = true
            )
        }
    }

    fun onHomeOpened() {
        val state = _state.value
        require(state is ItemDetailsUiState.Loaded)
        _state.value = state.copy(isHomeOpen = false)
    }

    fun onDeleteDialogDismissed() {
        val state = _state.value
        require(state is ItemDetailsUiState.Loaded)
        _state.value = state.copy(isDeleteDialogOpen = false)
    }

    fun onDeletingErrorShown() {
        val state = _state.value
        require(state is ItemDetailsUiState.Loaded)
        _state.value = state.copy(hasDeletingError = false)
    }

    fun onReload() {
        fetch()
    }
}

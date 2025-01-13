package jp.cordea.closet.ui.item_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.repository.ItemRepository
import jp.cordea.closet.repository.ThumbnailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val thumbnailRepository: ThumbnailRepository
) : ViewModel() {
    private val id = requireNotNull(savedStateHandle.get<String>("id"))

    private val retry = Channel<Unit>()
    private val isEditOpen = MutableStateFlow(false)
    private val isHomeOpen = MutableStateFlow(false)
    private val isDeleteDialogOpen = MutableStateFlow(false)
    private val hasDeletingError = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val item = retry.receiveAsFlow()
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(itemRepository.find(id))
            }.map {
                ItemDetailsUiState.Loaded(
                    id = it.id,
                    type = it.type,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    imagePath = it.imagePath,
                    values = it.asMap(),
                    tags = it.tags
                ) as ItemDetailsUiState
            }.catch {
                emit(ItemDetailsUiState.Failed)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ItemDetailsUiState.Loading
        )

    val state = combine(
        item, isEditOpen, isHomeOpen, isDeleteDialogOpen, hasDeletingError
    ) { item, isEditOpen, isHomeOpen, isDeleteDialogOpen, hasDeletingError ->
        when (item) {
            ItemDetailsUiState.Failed -> item
            ItemDetailsUiState.Loading -> item
            is ItemDetailsUiState.Loaded -> item.copy(
                isEditOpen = isEditOpen,
                isHomeOpen = isHomeOpen,
                isDeleteDialogOpen = isDeleteDialogOpen,
                hasDeletingError = hasDeletingError
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ItemDetailsUiState.Loading
    )

    fun onEditClicked() {
        isEditOpen.value = true
    }

    fun onEditOpened() {
        isEditOpen.value = false
    }

    fun onDeleteClicked() {
        isDeleteDialogOpen.value = true
    }

    fun onDeleteDialogConfirmed() {
        val state = state.value
        require(state is ItemDetailsUiState.Loaded)
        viewModelScope.launch {
            isDeleteDialogOpen.value = false
            runCatching {
                itemRepository.delete(state.id)
            }.onFailure {
                hasDeletingError.value = true
                return@launch
            }
            runCatching {
                thumbnailRepository.delete(state.imagePath)
            }
            hasDeletingError.value = false
            isHomeOpen.value = true
        }
    }

    fun onHomeOpened() {
        isHomeOpen.value = false
    }

    fun onDeleteDialogDismissed() {
        isDeleteDialogOpen.value = false
    }

    fun onDeletingErrorShown() {
        hasDeletingError.value = false
    }

    fun onReload() {
        viewModelScope.launch {
            retry.send(Unit)
        }
    }
}

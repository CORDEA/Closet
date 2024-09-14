package jp.cordea.closet.ui.add_item

import android.net.Uri
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.data.Item
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.repository.ItemRepository
import jp.cordea.closet.repository.ThumbnailRepository
import jp.cordea.closet.ui.toKeyboardType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
    private val thumbnailRepository: ThumbnailRepository
) : ViewModel() {
    private val type = savedStateHandle.get<String>("type")?.let {
        if (it.isBlank()) {
            null
        } else {
            ItemType.valueOf(it)
        }
    }
    private val id = savedStateHandle.get<String>("id")

    private val retry = Channel<Unit>()
    private val hasTitleError = MutableStateFlow(false)
    private val hasAddingError = MutableStateFlow(false)
    private val isHomeOpen = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val item = retry.receiveAsFlow()
        .onStart { emit(Unit) }
        .flatMapLatest {
            if (type != null) {
                flowOf(AddItemUiState.Loaded(type = type))
            } else {
                flow {
                    emit(itemRepository.find(requireNotNull(id)))
                }.map {
                    initialItem = it
                    AddItemUiState.Loaded(
                        type = it.type,
                        imagePath = it.imagePath,
                        values = it.asMap(),
                        tags = it.tags
                    ) as AddItemUiState
                }.catch {
                    emit(AddItemUiState.Failed)
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddItemUiState.Loading
        )
    private val editingValues = MutableStateFlow<Map<ItemAttribute, String>?>(null)
    private val editingTags = MutableStateFlow<List<String>?>(null)
    private val editingImagePath = MutableStateFlow<String?>(null)

    val state = combine(
        item,
        hasTitleError,
        hasAddingError,
        isHomeOpen,
        combine(
            editingValues,
            editingTags,
            editingImagePath
        ) { values, tags, path -> Triple(values, tags, path) }
    ) { item,
        hasTitleError,
        hasAddingError,
        isHomeOpen,
        editing ->
        when (item) {
            AddItemUiState.Failed -> item
            AddItemUiState.Loading -> item
            is AddItemUiState.Loaded -> item.copy(
                hasTitleError = hasTitleError,
                hasAddingError = hasAddingError,
                isHomeOpen = isHomeOpen,
                values = editing.first ?: item.values,
                tags = editing.second ?: item.tags,
                imagePath = editing.third ?: item.imagePath
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AddItemUiState.Loading
    )

    private var initialItem: Item? = null
    private var thumbnail: Uri? = null

    fun onTextChanged(attribute: ItemAttribute, value: String) {
        val type = attribute.toKeyboardType()
        if (type == KeyboardType.Number && value.isNotEmpty() && value.toDoubleOrNull() == null) {
            return
        }
        val state = state.value
        require(state is AddItemUiState.Loaded)
        val hasError = if (attribute == ItemAttribute.TITLE && value.isNotBlank()) {
            false
        } else {
            hasTitleError.value
        }
        hasTitleError.value = hasError
        editingValues.value = state.values + (attribute to value)
    }

    fun onTextSubmitted(attribute: ItemAttribute) {
        if (attribute != ItemAttribute.TAG) {
            return
        }
        val state = state.value
        require(state is AddItemUiState.Loaded)
        val tag = state.values[ItemAttribute.TAG]
        if (tag.isNullOrBlank() || tag in state.tags) {
            return
        }
        editingTags.value = state.tags + listOf(tag)
        editingValues.value = state.values.toMutableMap().also {
            it[ItemAttribute.TAG] = ""
        }
    }

    fun onAddClicked() {
        val state = state.value
        require(state is AddItemUiState.Loaded)
        val title = state.values[ItemAttribute.TITLE] ?: ""
        if (title.isBlank()) {
            hasTitleError.value = true
            return
        }
        hasTitleError.value = false
        val oldImagePath = initialItem?.imagePath
        var newImagePath = oldImagePath ?: ""
        val thumbnail = thumbnail
        if (thumbnail != null) {
            runCatching {
                newImagePath = thumbnailRepository.insert(thumbnail)
            }.onFailure {
                hasAddingError.value = true
                return
            }
            if (oldImagePath != null) {
                runCatching {
                    thumbnailRepository.delete(oldImagePath)
                }.onFailure {
                    // TODO
                }
            }
        }
        val item = Item(
            id = initialItem?.id ?: UUID.randomUUID().toString(),
            title = title,
            description = state.values[ItemAttribute.DESCRIPTION] ?: "",
            createdAt = initialItem?.createdAt ?: Date(),
            updatedAt = Date(),
            type = state.type,
            imagePath = newImagePath,
            material = state.values[ItemAttribute.MATERIAL] ?: "",
            size = state.values[ItemAttribute.SIZE] ?: "",
            bust = state.values[ItemAttribute.BUST]?.toFloatOrNull() ?: 0f,
            length = state.values[ItemAttribute.LENGTH]?.toFloatOrNull() ?: 0f,
            height = state.values[ItemAttribute.HEIGHT]?.toFloatOrNull() ?: 0f,
            width = state.values[ItemAttribute.WIDTH]?.toFloatOrNull() ?: 0f,
            depth = state.values[ItemAttribute.DEPTH]?.toFloatOrNull() ?: 0f,
            waist = state.values[ItemAttribute.WAIST]?.toFloatOrNull() ?: 0f,
            hip = state.values[ItemAttribute.HIP]?.toFloatOrNull() ?: 0f,
            sleeveLength = state.values[ItemAttribute.SLEEVE_LENGTH]?.toFloatOrNull() ?: 0f,
            shoulderWidth = state.values[ItemAttribute.SHOULDER_WIDTH]?.toFloatOrNull()
                ?: 0f,
            neckSize = state.values[ItemAttribute.NECK_SIZE]?.toFloatOrNull() ?: 0f,
            inseam = state.values[ItemAttribute.INSEAM]?.toFloatOrNull() ?: 0f,
            rise = state.values[ItemAttribute.RISE]?.toFloatOrNull() ?: 0f,
            legOpening = state.values[ItemAttribute.LEG_OPENING]?.toFloatOrNull() ?: 0f,
            knee = state.values[ItemAttribute.KNEE]?.toFloatOrNull() ?: 0f,
            thigh = state.values[ItemAttribute.THIGH]?.toFloatOrNull() ?: 0f,
            headCircumference = state.values[ItemAttribute.HEAD_CIRCUMFERENCE]?.toFloatOrNull()
                ?: 0f,
            tags = state.tags
        )
        viewModelScope.launch {
            runCatching {
                if (initialItem == null) {
                    itemRepository.insert(item)
                } else {
                    itemRepository.update(item)
                }
            }.onFailure {
                hasAddingError.value = true
            }.onSuccess {
                isHomeOpen.value = true
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
        uri?.let {
            thumbnail = it
            editingImagePath.value = it.toString()
        }
    }

    fun onHomeOpened() {
        isHomeOpen.value = false
    }

    fun onTagClicked(value: String) {
        val state = state.value
        require(state is AddItemUiState.Loaded)
        editingTags.value = state.tags - value
    }

    fun onReload() {
        viewModelScope.launch {
            retry.send(Unit)
        }
    }

    fun onAddingErrorShown() {
        hasAddingError.value = false
    }
}

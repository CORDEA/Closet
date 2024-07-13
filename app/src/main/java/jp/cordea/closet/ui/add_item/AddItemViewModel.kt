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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _state = MutableStateFlow<AddItemUiState>(AddItemUiState.Loading)
    val state get() = _state.asStateFlow()

    private val type: ItemType? = savedStateHandle.get<String>("type")?.let {
        if (it.isBlank()) {
            null
        } else {
            ItemType.valueOf(it)
        }
    }
    private val id: String? = savedStateHandle.get<String>("id")

    private var editingItem: Item? = null

    init {
        load()
    }

    private fun load() {
        if (type != null) {
            _state.value = AddItemUiState.Loaded(type = type)
            return
        }
        val id = requireNotNull(id)
        viewModelScope.launch {
            val result = runCatching {
                itemRepository.find(id)
            }
            val item = result.getOrNull()
            if (item == null) {
                _state.value = AddItemUiState.Failed
                return@launch
            }
            editingItem = item
            _state.value = AddItemUiState.Loaded(
                type = item.type,
                imagePath = item.imagePath,
                values = mapOf(
                    ItemAttribute.TITLE to item.title,
                    ItemAttribute.DESCRIPTION to item.description,
                    ItemAttribute.MATERIAL to item.material,
                    ItemAttribute.SIZE to item.size,
                    ItemAttribute.BUST to item.bust.toString(),
                    ItemAttribute.LENGTH to item.length.toString(),
                    ItemAttribute.HEIGHT to item.height.toString(),
                    ItemAttribute.WIDTH to item.width.toString(),
                    ItemAttribute.DEPTH to item.depth.toString(),
                    ItemAttribute.WAIST to item.waist.toString(),
                    ItemAttribute.HIP to item.hip.toString(),
                    ItemAttribute.SLEEVE_LENGTH to item.sleeveLength.toString(),
                    ItemAttribute.SHOULDER_WIDTH to item.shoulderWidth.toString(),
                    ItemAttribute.NECK_SIZE to item.neckSize.toString(),
                    ItemAttribute.INSEAM to item.inseam.toString(),
                    ItemAttribute.RISE to item.rise.toString(),
                    ItemAttribute.LEG_OPENING to item.legOpening.toString(),
                    ItemAttribute.KNEE to item.knee.toString(),
                    ItemAttribute.THIGH to item.thigh.toString(),
                    ItemAttribute.HEAD_CIRCUMFERENCE to item.headCircumference.toString(),
                ),
                tags = item.tags
            )
        }
    }

    fun onTextChanged(attribute: ItemAttribute, value: String) {
        val type = attribute.toKeyboardType()
        if (type == KeyboardType.Number && value.isNotEmpty() && value.toDoubleOrNull() == null) {
            return
        }
        val state = _state.value
        if (state !is AddItemUiState.Loaded) {
            return
        }
        val hasTitleError = if (attribute == ItemAttribute.TITLE && value.isNotBlank()) {
            false
        } else {
            state.hasTitleError
        }
        _state.value = state.copy(
            values = state.values + (attribute to value),
            hasTitleError = hasTitleError
        )
    }

    fun onTextSubmitted(attribute: ItemAttribute) {
        if (attribute != ItemAttribute.TAG) {
            return
        }
        val state = _state.value
        if (state !is AddItemUiState.Loaded) {
            return
        }
        val tag = state.values[ItemAttribute.TAG]
        if (tag.isNullOrBlank() || tag in state.tags) {
            return
        }
        _state.value = state.copy(
            tags = state.tags + listOf(tag),
            values = state.values.toMutableMap().also {
                it[ItemAttribute.TAG] = ""
            }
        )
    }

    fun onAddClicked() {
        val editingItem = editingItem
        val state = _state.value
        if (state !is AddItemUiState.Loaded) {
            return
        }
        val title = state.values[ItemAttribute.TITLE] ?: ""
        if (title.isBlank()) {
            _state.value = state.copy(hasTitleError = true)
            return
        }
        val item = Item(
            id = editingItem?.id ?: UUID.randomUUID().toString(),
            title = title,
            description = state.values[ItemAttribute.DESCRIPTION] ?: "",
            createdAt = editingItem?.createdAt ?: Date(),
            updatedAt = Date(),
            type = state.type,
            imagePath = state.imagePath,
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
            if (editingItem == null) {
                itemRepository.insert(item)
            } else {
                itemRepository.update(item)
            }
        }
        _state.value = state.copy(
            isHomeOpen = true,
            hasTitleError = false
        )
    }

    fun onImageSelected(uri: Uri?) {
        val state = _state.value
        if (state !is AddItemUiState.Loaded) {
            return
        }
        uri?.let {
            val url = thumbnailRepository.insert(it)
            _state.value = state.copy(
                imagePath = url
            )
        }
    }

    fun onHomeOpened() {
        val state = _state.value
        require(state is AddItemUiState.Loaded)
        _state.value = state.copy(
            isHomeOpen = false
        )
    }

    fun onTagClicked(value: String) {
        val state = _state.value
        if (state !is AddItemUiState.Loaded) {
            return
        }
        _state.value = state.copy(
            tags = state.tags - value
        )
    }

    fun onReload() {
        load()
    }
}

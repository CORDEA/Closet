package jp.cordea.closet.ui.add_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.data.Item
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddItemUiState())
    val state get() = _state.asStateFlow()

    fun onTextChanged(attribute: ItemAttribute, value: String) {
        val state = _state.value
        _state.value = state.copy(
            values = state.values + (attribute to value)
        )
    }

    fun onTextSubmitted(attribute: ItemAttribute) {
        if (attribute != ItemAttribute.TAG) {
            return
        }
        val state = _state.value
        val tag = state.values[ItemAttribute.TAG]
        if (tag.isNullOrBlank()) {
            return
        }
        _state.value = state.copy(
            tags = state.tags + listOf(tag)
        )
    }

    fun onAddClicked() {
        val state = _state.value
        viewModelScope.launch {
            repository.insert(
                Item(
                    id = UUID.randomUUID().toString(),
                    type = state.type,
                    createdAt = Date(),
                    updatedAt = Date(),
                    imagePath = "",
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
            )
        }
    }
}

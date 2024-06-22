package jp.cordea.closet.ui.item_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.data.ItemAttribute
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

    private val _state = MutableStateFlow(ItemDetailsUiState())
    val state get() = _state.asStateFlow()

    init {
        val id = requireNotNull(savedStateHandle.get<String>("id"))
        viewModelScope.launch {
            val item = repository.find(id)
            _state.value = ItemDetailsUiState(
                id = item.id,
                type = item.type,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt,
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

    fun onEditClicked() {
        val id = _state.value.id
        if (id.isBlank()) {
            return
        }
        _state.value = _state.value.copy(isEditOpen = true)
    }

    fun onEditOpened() {
        _state.value = _state.value.copy(isEditOpen = false)
    }
}

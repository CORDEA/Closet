package jp.cordea.closet.ui.tagged_items

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaggedItemsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
}

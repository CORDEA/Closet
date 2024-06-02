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
        viewModelScope.launch {
            _state.value = HomeUiState.Loaded(
                items = repository.findAll()
                    .map { HomeItem(it.id, "", it.imagePath) }
            )
        }
    }
}

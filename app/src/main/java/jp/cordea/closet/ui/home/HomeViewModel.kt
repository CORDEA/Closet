package jp.cordea.closet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.repository.ItemRepository
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
class HomeViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val isSearchExpanded = MutableStateFlow(false)
    private val retry = Channel<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val items = retry.receiveAsFlow()
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(repository.findAll())
            }.map {
                HomeUiState.Loaded(
                    it.map {
                        HomeItem(
                            it.id,
                            it.title,
                            it.imagePath,
                            it.tags
                        )
                    }
                ) as HomeUiState
            }.catch {
                emit(HomeUiState.Failed)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState.Loading
        )

    val state =
        combine(searchQuery, isSearchExpanded, items) { query, expanded, items ->
            when (items) {
                HomeUiState.Failed -> items
                HomeUiState.Loading -> items
                is HomeUiState.Loaded -> items.copy(
                    searchQuery = query,
                    isSearchExpanded = expanded
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState.Loading
        )

    fun onAdded() {
        onReload()
    }

    fun onSearchExpanded(expanded: Boolean) {
        isSearchExpanded.value = expanded
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onReload() {
        viewModelScope.launch {
            retry.send(Unit)
        }
    }
}

package jp.cordea.closet.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.repository.ItemRepository
import jp.cordea.closet.repository.ItemTypeRepository
import jp.cordea.closet.repository.TagRepository
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
    private val itemRepository: ItemRepository,
    private val itemTypeRepository: ItemTypeRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val isSearchExpanded = MutableStateFlow(false)
    private val showTypes = MutableStateFlow(false)
    private val showTags = MutableStateFlow(false)
    private val retry = Channel<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val items = retry.receiveAsFlow()
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(itemRepository.findAll())
            }.map {
                it.map {
                    HomeItem(
                        it.id,
                        it.title,
                        it.imagePath,
                        it.tags
                    )
                }
            }
        }

    private val allTypes = flow {
        emit(itemTypeRepository.findAll().toTypedArray())
    }.catch {
        emit(emptyArray())
    }
    private val selectedTypes = MutableStateFlow<List<ItemType>>(emptyList())
    private val types = combine(allTypes, selectedTypes) { allTypes, selectedTypes ->
        allTypes.associateWith { selectedTypes.contains(it) }
    }

    private val allTags = flow {
        emit(tagRepository.findAll())
    }.catch {
        emit(emptyList())
    }
    private val selectedTags = MutableStateFlow<List<String>>(emptyList())
    private val tags = combine(allTags, selectedTags) { allTags, selectedTags ->
        allTags.associateWith { selectedTags.contains(it) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val searchResult = combine(searchQuery, types, tags) { searchQuery, types, tags ->
        Triple(searchQuery, types, tags)
    }.flatMapLatest {
        flow {
            emit(
                itemRepository.findBy(
                    "%${it.first}%",
                    it.second.filter { it.value }.keys,
                    it.third.filter { it.value }.keys
                )
            )
        }.map {
            it.map {
                HomeItem(
                    it.id,
                    it.title,
                    it.imagePath,
                    it.tags
                )
            }
        }.catch {
            emit(emptyList())
        }
    }

    val state =
        combine(
            items,
            combine(
                searchQuery,
                isSearchExpanded,
                searchResult
            ) { searchQuery,
                isSearchExpanded,
                searchResult ->
                Triple(searchQuery, isSearchExpanded, searchResult)
            },
            combine(
                types,
                showTypes
            ) { types, showTypes ->
                Pair(types, showTypes)
            },
            combine(
                tags,
                showTags
            ) { tags, showTags ->
                Pair(tags, showTags)
            }
        ) { items, search, types, tags ->
            HomeUiState(
                state = LoadingState.LOADED,
                items = items,
                searchResult = search.third,
                searchQuery = search.first,
                isSearchExpanded = search.second,
                tags = tags.first,
                showTags = tags.second,
                types = types.first,
                showTypes = types.second
            )
        }.catch {
            emit(HomeUiState(LoadingState.FAILED))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeUiState(LoadingState.LOADING)
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

    fun onTypesClicked() {
        showTypes.value = true
    }

    fun onTagsClicked() {
        showTags.value = true
    }

    fun onTypesDismissed() {
        showTypes.value = false
    }

    fun onTagCheckedChanged(key: String, selected: Boolean) {
        selectedTags.value = if (selected) {
            selectedTags.value + key
        } else {
            selectedTags.value - key
        }
    }

    fun onTypeCheckedChanged(key: ItemType, selected: Boolean) {
        selectedTypes.value = if (selected) {
            selectedTypes.value + key
        } else {
            selectedTypes.value - key
        }
    }

    fun onTagsDismissed() {
        showTags.value = false
    }

    fun onReload() {
        viewModelScope.launch {
            retry.send(Unit)
        }
    }
}

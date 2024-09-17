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
import kotlin.collections.map
import kotlin.collections.toMap

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val itemTypeRepository: ItemTypeRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val isSearchExpanded = MutableStateFlow(false)
    private val retry = Channel<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val items = retry.receiveAsFlow()
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(itemRepository.findAll())
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

    private val allTypes = flow {
        emit(itemTypeRepository.findAll().toTypedArray())
    }.catch {
        emit(emptyArray())
    }
    private val selectedTypes = MutableStateFlow<List<ItemType>?>(null)
    private val types = combine(allTypes, selectedTypes) { allTypes, selectedTypes ->
        allTypes.map { it to (selectedTypes?.contains(it) != false) }.toMap()
    }

    private val allTags = flow {
        emit(tagRepository.findAll())
    }.catch {
        emit(emptyList())
    }
    private val selectedTags = MutableStateFlow<List<String>?>(null)
    private val tags = combine(allTags, selectedTags) { allTags, selectedTags ->
        allTags.map { it to (selectedTags?.contains(it) != false) }.toMap()
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
            types,
            tags
        ) { items, search, types, tags ->
            when (items) {
                HomeUiState.Failed -> items
                HomeUiState.Loading -> items
                is HomeUiState.Loaded -> items.copy(
                    searchQuery = search.first,
                    isSearchExpanded = search.second,
                    searchResult = search.third,
                    tags = tags,
                    types = types
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

package jp.cordea.closet.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import jp.cordea.closet.R
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.ui.toLocalizedString

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun Home(navController: NavController, viewModel: HomeViewModel) {
    LaunchedEffect(navController) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        val isAdded = handle?.remove<Boolean>("isAdded") ?: false
        if (isAdded) {
            viewModel.onAdded()
        }
        val isDeleted = handle?.remove<Boolean>("isDeleted") ?: false
        if (isDeleted) {
            viewModel.onDeleted()
        }
    }
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            val padding by animateDpAsState(
                if (state.isSearchExpanded) 0.dp else 16.dp,
                label = "Search bar"
            )
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onSearch = { viewModel.onSearchExpanded(true) },
                        expanded = state.isSearchExpanded,
                        onExpandedChange = viewModel::onSearchExpanded,
                        placeholder = {
                            Text(stringResource(R.string.home_search_placeholder))
                        },
                        leadingIcon = {
                            if (state.isSearchExpanded) {
                                IconButton(onClick = {
                                    viewModel.onSearchExpanded(false)
                                    viewModel.onSearchQueryChanged("")
                                }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            } else {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.onSearchExpanded(false)
                                viewModel.onSearchQueryChanged("")
                                navController.navigate("settings")
                            }) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        }
                    )
                },
                expanded = state.isSearchExpanded,
                onExpandedChange = viewModel::onSearchExpanded
            ) {
                if (state.showTags) {
                    TagsController(
                        state.tags,
                        onDismiss = {
                            viewModel.onTagsDismissed()
                        },
                        onCheckedChange = { key, selected ->
                            viewModel.onTagCheckedChanged(key, selected)
                        }
                    )
                }
                if (state.showTypes) {
                    TypesController(
                        state.types,
                        onDismiss = {
                            viewModel.onTypesDismissed()
                        },
                        onCheckedChange = { key, selected ->
                            viewModel.onTypeCheckedChanged(key, selected)
                        }
                    )
                }
                LazyColumn {
                    if (state.typesEnabled || state.tagsEnabled) {
                        item {
                            FlowRow(
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                if (state.typesEnabled) {
                                    FilterChip(
                                        selected = false,
                                        onClick = {
                                            viewModel.onTypesClicked()
                                        },
                                        label = {
                                            Text(stringResource(R.string.home_search_types))
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                if (state.tagsEnabled) {
                                    FilterChip(
                                        selected = false,
                                        onClick = {
                                            viewModel.onTagsClicked()
                                        },
                                        label = {
                                            Text(stringResource(R.string.home_search_tags))
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(state.searchResult) {
                        SearchResult(it) {
                            navController.navigate("item-details/${it.id}")
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("type-select")
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            )
        },
    ) { padding ->
        val layoutDirection = LocalLayoutDirection.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(layoutDirection),
                    end = padding.calculateEndPadding(layoutDirection),
                )
        ) {
            when (state.state) {
                LoadingState.FAILED -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.load_failure_body))
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = viewModel::onReload) {
                        Text(stringResource(R.string.load_failure_button))
                    }
                }

                LoadingState.LOADED -> LazyColumn(
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    )
                ) {
                    state.items.forEach {
                        item {
                            Item(it) {
                                navController.navigate("item-details/${it.id}")
                            }
                        }
                    }
                }

                LoadingState.LOADING -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun SearchResult(item: HomeItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun Item(item: HomeItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 19f / 10f)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = item.imagePath,
                contentScale = ContentScale.Crop,
                contentDescription = "Thumbnail"
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(vertical = 12.dp),
            ) {
                FlowRow(
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    item.tags.forEach {
                        AssistChip(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = {},
                            label = { Text(it) }
                        )
                    }
                }
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TypesController(
    values: Map<ItemType, Boolean>,
    onDismiss: () -> Unit,
    onCheckedChange: (ItemType, Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            items(values.toList()) { (tag, selected) ->
                SelectableItem(
                    title = tag.toLocalizedString(),
                    selected = selected,
                ) {
                    onCheckedChange(tag, it)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TagsController(
    values: Map<String, Boolean>,
    onDismiss: () -> Unit,
    onCheckedChange: (String, Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            items(values.toList()) { (tag, selected) ->
                SelectableItem(
                    title = tag,
                    selected = selected,
                ) {
                    onCheckedChange(tag, it)
                }
            }
        }
    }
}

@Composable
private fun SelectableItem(
    title: String,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selected,
                onCheckedChange = onCheckedChange,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title)
        }
    }
}

@Preview
@Composable
private fun Preview() {
//    Home(rememberNavController())
}

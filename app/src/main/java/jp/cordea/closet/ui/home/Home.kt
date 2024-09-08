package jp.cordea.closet.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun Home(navController: NavController, viewModel: HomeViewModel) {
    LaunchedEffect(navController) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        val isAdded = handle?.remove<Boolean>("isAdded") ?: false
        if (isAdded) {
            viewModel.onAdded()
        }
    }
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            val expanded = when (val e = state) {
                HomeUiState.Failed -> false
                is HomeUiState.Loaded -> e.isSearchExpanded
                HomeUiState.Loading -> false
            }
            val query = when (val e = state) {
                HomeUiState.Failed -> ""
                is HomeUiState.Loaded -> e.searchQuery
                HomeUiState.Loading -> ""
            }
            val padding by animateDpAsState(if (expanded) 0.dp else 16.dp, label = "Search bar")
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onSearch = { viewModel.onSearchExpanded(true) },
                        expanded = expanded,
                        onExpandedChange = viewModel::onSearchExpanded,
                        placeholder = {
                            Text(stringResource(R.string.home_search_placeholder))
                        },
                        leadingIcon = {
                            if (expanded) {
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
                expanded = expanded,
                onExpandedChange = viewModel::onSearchExpanded
            ) {
                LazyColumn {
                    item {
                        FlowRow(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            FilterChip(
                                selected = false,
                                onClick = {},
                                label = {
                                    Text(stringResource(R.string.home_search_types))
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = false,
                                onClick = {},
                                label = {
                                    Text(stringResource(R.string.home_search_tags))
                                }
                            )
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
            val state by viewModel.state.collectAsState()
            when (val e = state) {
                HomeUiState.Failed -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.load_failure_body))
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = viewModel::onReload) {
                        Text(stringResource(R.string.load_failure_button))
                    }
                }

                is HomeUiState.Loaded -> LazyColumn(
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    )
                ) {
                    e.items.forEach {
                        item {
                            Item(it) {
                                navController.navigate("item-details/${it.id}")
                            }
                        }
                    }
                }

                HomeUiState.Loading -> CircularProgressIndicator()
            }
        }
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

@Preview
@Composable
private fun Preview() {
//    Home(rememberNavController())
}

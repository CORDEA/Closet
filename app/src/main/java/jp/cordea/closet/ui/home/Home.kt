package jp.cordea.closet.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import jp.cordea.closet.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Home(navController: NavController, viewModel: HomeViewModel) {
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(navController) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        val isAdded = handle?.remove<Boolean>("isAdded") ?: false
        if (isAdded) {
            viewModel.onAdded()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Home")
                }, actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("settings")
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                scrollBehavior = behavior
            )
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
        modifier = Modifier.nestedScroll(behavior.nestedScrollConnection)
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
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.BottomStart),
                text = item.title,
                style = MaterialTheme.typography.headlineSmall
            )
        }

    }
}

@Preview
@Composable
private fun Preview() {
//    Home(rememberNavController())
}

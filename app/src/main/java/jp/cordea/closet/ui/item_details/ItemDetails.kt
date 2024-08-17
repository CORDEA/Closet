package jp.cordea.closet.ui.item_details

import android.text.format.DateFormat
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import jp.cordea.closet.R
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.ui.toLocalizedString
import java.util.Date

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemDetails(navController: NavController, viewModel: ItemDetailsViewModel) {
    val value by viewModel.state.collectAsState()
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = value.title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = value.canEdit,
                        onClick = viewModel::onEditClicked
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }

                },
                scrollBehavior = behavior
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
            when (val e = value) {
                ItemDetailsUiState.Failed -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.load_failure_body))
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = viewModel::onReload) {
                        Text(stringResource(R.string.load_failure_button))
                    }
                }

                is ItemDetailsUiState.Loaded -> Body(navController, viewModel, e)
                ItemDetailsUiState.Loading -> CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun Body(
    navController: NavController,
    viewModel: ItemDetailsViewModel,
    state: ItemDetailsUiState.Loaded
) {
    LaunchedEffect(state.isEditOpen) {
        if (state.isEditOpen) {
            navController.navigate("add-item?id=${state.id}")
            viewModel.onEditOpened()
        }
    }
    LazyColumn(
        contentPadding = PaddingValues(
            top = 16.dp,
            start = 16.dp,
            end = 16.dp,
            bottom = 32.dp
        )
    ) {
        if (state.showThumbnail) {
            item { Thumbnail(state.imagePath) }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
        }
        if (state.showDescription) {
            item {
                Item(state, ItemAttribute.DESCRIPTION)
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        item {
            Item(state, ItemAttribute.SIZE)
        }
        content(state, state.type)
        item {
            Item(state, ItemAttribute.MATERIAL)
        }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item { Tag(state) }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            DateItem(stringResource(R.string.created_at), state.createdAt)
        }
        item {
            DateItem(stringResource(R.string.updated_at), state.updatedAt)
        }
    }
}

private fun LazyListScope.content(state: ItemDetailsUiState.Loaded, type: ItemType) {
    items(
        count = type.attributes.size,
        itemContent = {
            Item(state, type.attributes.elementAt(it))
        }
    )
}

@Composable
private fun Thumbnail(imagePath: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 19f / 10f)
            .clip(RoundedCornerShape(percent = 16))
    ) {
        AsyncImage(
            model = imagePath,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = "Thumbnail"
        )
    }
}

@Composable
private fun Item(state: ItemDetailsUiState.Loaded, attribute: ItemAttribute) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = attribute.toLocalizedString(),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = state.values.getOrDefault(attribute, ""),
        )
    }
}

@Composable
private fun DateItem(label: String, date: Date) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = DateFormat.format("yyyy/MM/dd HH:mm", date).toString(),
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun Tag(state: ItemDetailsUiState.Loaded) {
    FlowRow {
        state.tags.forEach {
            AssistChip(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {},
                label = { Text(it) }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
//    ItemDetails(ItemType.OUTERWEAR)
}

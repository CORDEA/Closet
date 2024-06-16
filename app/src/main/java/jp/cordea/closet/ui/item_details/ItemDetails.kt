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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import jp.cordea.closet.R
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.ui.toLocalizedString
import java.util.Date

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ItemDetails(viewModel: ItemDetailsViewModel) {
    val value by viewModel.state.collectAsState()
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = value.values.getOrDefault(ItemAttribute.TITLE, ""))
                },
                actions = {
                    IconButton(
                        onClick = {}
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
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp
                )
            ) {
                item { Thumbnail(viewModel) }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    Item(viewModel, ItemAttribute.DESCRIPTION)
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                item {
                    Item(viewModel, ItemAttribute.SIZE)
                }
                content(viewModel, value.type)
                item {
                    Item(viewModel, ItemAttribute.MATERIAL)
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                item { Tag() }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                item {
                    DateItem(stringResource(R.string.created_at), value.createdAt)
                }
                item {
                    DateItem(stringResource(R.string.updated_at), value.updatedAt)
                }
            }
        }
    }
}

private fun LazyListScope.content(viewModel: ItemDetailsViewModel, type: ItemType) {
    items(
        count = type.attributes.size,
        itemContent = {
            Item(viewModel, type.attributes.elementAt(it))
        }
    )
}

@Composable
private fun Thumbnail(viewModel: ItemDetailsViewModel) {
    val value by viewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 19f / 10f)
            .clip(RoundedCornerShape(percent = 16))
    ) {
        AsyncImage(
            model = value.imagePath,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = "Thumbnail"
        )
    }
}

@Composable
private fun Item(viewModel: ItemDetailsViewModel, attribute: ItemAttribute) {
    val value by viewModel.state.collectAsState()
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = attribute.toLocalizedString(),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = value.values.getOrDefault(attribute, ""),
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
private fun Tag() {
    FlowRow {
        Chip()
        Chip()
        Chip()
        Chip()
        Chip()
        Chip()
        Chip()
    }
}

@Composable
private fun Chip() {
    AssistChip(
        modifier = Modifier.padding(horizontal = 4.dp),
        onClick = { },
        label = { Text(text = "text") }
    )
}

@Preview
@Composable
private fun Preview() {
//    ItemDetails(ItemType.OUTERWEAR)
}

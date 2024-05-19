package jp.cordea.closet.ui.add_item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.cordea.closet.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddItem() {
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Item")
                },
                scrollBehavior = behavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.Check,
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
            LazyColumn(
                contentPadding = PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp
                )
            ) {
                item { Thumbnail() }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                item {
                    Field()
                }
                item {
                    Field()
                }
                item {
                    Field()
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                item { Tag() }
                item {
                    Field()
                }
            }
        }
    }
}

@Composable
private fun Thumbnail() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 19f / 10f)
            .clip(RoundedCornerShape(percent = 16))
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentScale = ContentScale.Crop,
            contentDescription = "Thumbnail"
        )
    }
}

@Composable
private fun Field() {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = CircleShape,
        value = "",
        onValueChange = { }
    )
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
    AddItem()
}

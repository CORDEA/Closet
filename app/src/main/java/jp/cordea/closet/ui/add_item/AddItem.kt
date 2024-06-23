package jp.cordea.closet.ui.add_item

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.ui.toKeyboardType
import jp.cordea.closet.ui.toLocalizedString

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddItem(navController: NavController, viewModel: AddItemViewModel) {
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val value by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Item")
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
                scrollBehavior = behavior
            )
        },
        floatingActionButton = {
            if (value.canAdd) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onAddClicked()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Add"
                        )
                    }
                )
            }
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
                AddItemUiState.Failed -> TODO()
                is AddItemUiState.Loaded -> Body(navController, viewModel, e)
                AddItemUiState.Loading -> CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun Body(
    navController: NavController,
    viewModel: AddItemViewModel,
    value: AddItemUiState.Loaded
) {
    LaunchedEffect(value.isHomeOpen) {
        if (value.isHomeOpen) {
            navController.popBackStack(route = "home", inclusive = false)
            navController.currentBackStackEntry?.savedStateHandle?.set("isAdded", true)
            viewModel.onHomeOpened()
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
        item { Thumbnail(viewModel, value) }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item {
            Field(viewModel, value, ItemAttribute.TITLE)
        }
        item {
            DescriptionField(viewModel, value)
        }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item {
            Field(viewModel, value, ItemAttribute.SIZE)
        }
        content(viewModel, value, value.type)
        item {
            Field(viewModel, value, ItemAttribute.MATERIAL)
        }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item { Tag(viewModel, value) }
        item {
            Field(viewModel, value, ItemAttribute.TAG)
        }
    }
}

private fun LazyListScope.content(
    viewModel: AddItemViewModel,
    value: AddItemUiState.Loaded,
    type: ItemType
) {
    items(
        count = type.attributes.size,
        itemContent = {
            Field(viewModel, value, type.attributes.elementAt(it))
        }
    )
}

@Composable
private fun Thumbnail(viewModel: AddItemViewModel, value: AddItemUiState.Loaded) {
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        viewModel::onImageSelected
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 19f / 10f)
            .clip(RoundedCornerShape(percent = 16))
            .clickable {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            model = value.imagePath,
            contentScale = ContentScale.Crop,
            contentDescription = "Thumbnail"
        )
    }
}

@Composable
private fun Field(
    viewModel: AddItemViewModel,
    value: AddItemUiState.Loaded,
    attribute: ItemAttribute,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = CircleShape,
        label = {
            Text(text = attribute.toLocalizedString())
        },
        value = value.values.getOrDefault(attribute, ""),
        keyboardActions = KeyboardActions(
            onDone = {
                viewModel.onTextSubmitted(attribute)
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = attribute.toKeyboardType()
        ),
        onValueChange = {
            viewModel.onTextChanged(attribute, it)
        },
        singleLine = true
    )
}

@Composable
private fun DescriptionField(viewModel: AddItemViewModel, value: AddItemUiState.Loaded) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(32.dp),
        label = {
            Text(text = ItemAttribute.DESCRIPTION.toLocalizedString())
        },
        value = value.values.getOrDefault(ItemAttribute.DESCRIPTION, ""),
        keyboardActions = KeyboardActions(
            onDone = {
                viewModel.onTextSubmitted(ItemAttribute.DESCRIPTION)
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = ItemAttribute.DESCRIPTION.toKeyboardType()
        ),
        onValueChange = {
            viewModel.onTextChanged(ItemAttribute.DESCRIPTION, it)
        },
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun Tag(viewModel: AddItemViewModel, value: AddItemUiState.Loaded) {
    FlowRow {
        value.tags.forEach {
            Chip(viewModel, it)
        }
    }
}

@Composable
private fun Chip(viewModel: AddItemViewModel, value: String) {
    AssistChip(
        modifier = Modifier.padding(horizontal = 4.dp),
        onClick = {
            viewModel.onTagClicked(value)
        },
        label = { Text(value) }
    )
}

@Preview
@Composable
private fun Preview() {
//    AddItem(ItemType.OUTERWEAR)
}

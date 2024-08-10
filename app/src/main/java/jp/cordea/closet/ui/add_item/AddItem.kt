package jp.cordea.closet.ui.add_item

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import jp.cordea.closet.R
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.ui.toKeyboardType
import jp.cordea.closet.ui.toLocalizedString

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
            if (value.canAdd && !WindowInsets.isImeVisible) {
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
                AddItemUiState.Failed -> Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(R.string.load_failure_body))
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = viewModel::onReload) {
                        Text(stringResource(R.string.load_failure_button))
                    }
                }

                is AddItemUiState.Loaded -> Body(navController, viewModel, e)
                AddItemUiState.Loading -> CircularProgressIndicator()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun Body(
    navController: NavController,
    viewModel: AddItemViewModel,
    state: AddItemUiState.Loaded
) {
    LaunchedEffect(state.isHomeOpen) {
        if (state.isHomeOpen) {
            navController.popBackStack(route = "home", inclusive = false)
            navController.currentBackStackEntry?.savedStateHandle?.set("isAdded", true)
            viewModel.onHomeOpened()
        }
    }
    val context = LocalContext.current
    LaunchedEffect(state.hasAddingError) {
        if (state.hasAddingError) {
            Toast
                .makeText(context, R.string.add_failure_error, Toast.LENGTH_SHORT)
                .show()
            viewModel.onAddingErrorShown()
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
        item { Thumbnail(state, viewModel::onImageSelected) }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item {
            Field(
                state,
                ItemAttribute.TITLE,
                onTextChange = { viewModel.onTextChanged(ItemAttribute.TITLE, it) },
                onTextSubmit = { viewModel.onTextSubmitted(ItemAttribute.TITLE) },
                error = if (state.hasTitleError) stringResource(R.string.attribute_title_error) else "",
            )
        }
        item {
            DescriptionField(
                state,
                onTextChange = { viewModel.onTextChanged(ItemAttribute.DESCRIPTION, it) },
                onTextSubmit = { viewModel.onTextSubmitted(ItemAttribute.DESCRIPTION) }
            )
        }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item {
            Field(state, ItemAttribute.SIZE,
                onTextChange = { viewModel.onTextChanged(ItemAttribute.SIZE, it) },
                onTextSubmit = { viewModel.onTextSubmitted(ItemAttribute.SIZE) }
            )
        }
        items(
            count = state.type.attributes.size,
            itemContent = {
                val attr = state.type.attributes.elementAt(it)
                Field(
                    state,
                    attr,
                    onTextChange = { viewModel.onTextChanged(attr, it) },
                    onTextSubmit = { viewModel.onTextSubmitted(attr) }
                )
            }
        )
        item {
            Field(
                state,
                ItemAttribute.MATERIAL,
                onTextChange = { viewModel.onTextChanged(ItemAttribute.MATERIAL, it) },
                onTextSubmit = { viewModel.onTextSubmitted(ItemAttribute.MATERIAL) }
            )
        }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        item {
            FlowRow {
                state.tags.forEach {
                    Chip(it) {
                    }
                }
            }
        }
        item {
            Field(
                state,
                ItemAttribute.TAG,
                onTextChange = { viewModel.onTextChanged(ItemAttribute.TAG, it) },
                onTextSubmit = { viewModel.onTextSubmitted(ItemAttribute.TAG) }
            )
        }
    }
}

@Composable
private fun Thumbnail(state: AddItemUiState.Loaded, onImageSelect: (Uri?) -> Unit) {
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        onImageSelect,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 19f / 10f)
            .clip(RoundedCornerShape(percent = 16))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
    ) {
        if (state.imagePath.isBlank()) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "Add"
            )
        } else {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = state.imagePath,
                contentScale = ContentScale.Crop,
                contentDescription = "Thumbnail"
            )
        }

    }
}

@Composable
private fun Field(
    state: AddItemUiState.Loaded,
    attribute: ItemAttribute,
    onTextSubmit: () -> Unit,
    onTextChange: (String) -> Unit,
    error: String = ""
) {
    Column {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = CircleShape,
            isError = error.isNotBlank(),
            label = {
                Text(text = attribute.toLocalizedString())
            },
            value = state.values.getOrDefault(attribute, ""),
            keyboardActions = KeyboardActions(
                onDone = { onTextSubmit() }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = attribute.toKeyboardType()
            ),
            onValueChange = onTextChange,
            singleLine = true
        )
        if (error.isNotBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun DescriptionField(
    state: AddItemUiState.Loaded,
    onTextSubmit: () -> Unit,
    onTextChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(32.dp),
        label = {
            Text(text = ItemAttribute.DESCRIPTION.toLocalizedString())
        },
        value = state.values.getOrDefault(ItemAttribute.DESCRIPTION, ""),
        keyboardActions = KeyboardActions(
            onDone = { onTextSubmit() }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = ItemAttribute.DESCRIPTION.toKeyboardType()
        ),
        onValueChange = onTextChange
    )
}

@Composable
private fun Chip(value: String, onClick: () -> Unit) {
    AssistChip(
        modifier = Modifier.padding(horizontal = 4.dp),
        onClick = onClick,
        label = { Text(value) }
    )
}

@Preview
@Composable
private fun Preview() {
//    AddItem(ItemType.OUTERWEAR)
}

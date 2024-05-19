package jp.cordea.closet.ui.type_select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.cordea.closet.data.ItemType

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TypeSelect() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Select")
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                ItemType.entries.forEach { type ->
                    item { Item(type = type) }
                }
            }
        }
    }
}

@Composable
private fun Item(type: ItemType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        onClick = {}
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(12.dp),
            text = type.toString(),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TypeSelect()
}

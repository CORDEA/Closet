package jp.cordea.closet.ui.type_select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import jp.cordea.closet.data.ItemType

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TypeSelect(navController: NavController) {
    val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Select")
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
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 32.dp
                )
            ) {
                ItemType.entries.forEach { type ->
                    item { Item(type, navController) }
                }
            }
        }
    }
}

@Composable
private fun Item(type: ItemType, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        onClick = {
            navController.navigate("add-item/${type}")
        }
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            text = type.toString(),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TypeSelect(rememberNavController())
}

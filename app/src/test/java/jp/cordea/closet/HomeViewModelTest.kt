package jp.cordea.closet

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import jp.cordea.closet.data.Item
import jp.cordea.closet.repository.ItemRepository
import jp.cordea.closet.ui.home.HomeItem
import jp.cordea.closet.ui.home.HomeUiState
import jp.cordea.closet.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @MockK
    private lateinit var repository: ItemRepository

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun init() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val item1 = mockk<Item> {
            every { id } returns "1"
            every { title } returns "title1"
            every { imagePath } returns "image1"
            every { tags } returns listOf("tag1")
        }
        val item2 = mockk<Item> {
            every { id } returns "2"
            every { title } returns "title2"
            every { imagePath } returns "image2"
            every { tags } returns listOf("tag2")
        }
        coEvery { repository.findAll() } returns listOf(item1, item2)

        val viewModel = HomeViewModel(repository)
        val results = mutableListOf<HomeUiState>()
        backgroundScope.launch(testDispatcher) { viewModel.state.toList(results) }

        try {
            assertThat(results).hasSize(1)
            assertThat((results.first() as HomeUiState.Loaded).items).isEqualTo(
                listOf(
                    HomeItem("1", "title1", "image1", listOf("tag1")),
                    HomeItem("2", "title2", "image2", listOf("tag2")),
                )
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `error on init`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.findAll() } throws IllegalArgumentException()

        val viewModel = HomeViewModel(repository)
        val results = mutableListOf<HomeUiState>()
        backgroundScope.launch(testDispatcher) { viewModel.state.toList(results) }

        try {
            assertThat(results).hasSize(1)
            assertThat(results.first()).isInstanceOf(HomeUiState.Failed::class.java)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

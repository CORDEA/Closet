package jp.cordea.closet

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import jp.cordea.closet.data.Item
import jp.cordea.closet.data.ItemAttribute
import jp.cordea.closet.data.ItemType
import jp.cordea.closet.repository.ItemRepository
import jp.cordea.closet.repository.ThumbnailRepository
import jp.cordea.closet.ui.add_item.AddItemUiState
import jp.cordea.closet.ui.add_item.AddItemViewModel
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

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AddItemVieModelTest {
    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var itemRepository: ItemRepository

    @MockK
    private lateinit var thumbnailRepository: ThumbnailRepository

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `init with type`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("type") } returns "TOPS"
        every { savedStateHandle.get<String>("id") } returns ""

        val viewModel = AddItemViewModel(savedStateHandle, itemRepository, thumbnailRepository)
        val results = mutableListOf<AddItemUiState>()
        backgroundScope.launch(testDispatcher) { viewModel.state.toList(results) }

        try {
            assertThat(results).hasSize(1)
            assertThat(results.first()).isEqualTo(AddItemUiState.Loaded(type = ItemType.TOPS))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `init with ID`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("type") } returns ""
        every { savedStateHandle.get<String>("id") } returns "1"
        val item = mockk<Item> {
            every { type } returns ItemType.TOPS
            every { id } returns "1"
            every { title } returns "title"
            every { description } returns "description"
            every { imagePath } returns "image"
            every { size } returns "size"
            every { material } returns "material"
            every { tags } returns listOf("tag")
            every { bust } returns 1.1f
            every { length } returns 2f
            every { sleeveLength } returns 3f
            every { shoulderWidth } returns 4f
            every { neckSize } returns 5f
            every { asMap() } answers { callOriginal() }
        }
        coEvery { itemRepository.find("1") } returns item

        val viewModel = AddItemViewModel(savedStateHandle, itemRepository, thumbnailRepository)
        val results = mutableListOf<AddItemUiState>()
        backgroundScope.launch(testDispatcher) {
            viewModel.state.toList(results)
        }

        try {
            assertThat(results).hasSize(1)
            assertThat(results.first()).isEqualTo(
                AddItemUiState.Loaded(
                    type = ItemType.TOPS,
                    imagePath = "image",
                    values = mapOf(
                        ItemAttribute.TITLE to "title",
                        ItemAttribute.DESCRIPTION to "description",
                        ItemAttribute.MATERIAL to "material",
                        ItemAttribute.SIZE to "size",
                        ItemAttribute.BUST to "1.1",
                        ItemAttribute.LENGTH to "2",
                        ItemAttribute.HEIGHT to "0",
                        ItemAttribute.WIDTH to "0",
                        ItemAttribute.DEPTH to "0",
                        ItemAttribute.WAIST to "0",
                        ItemAttribute.HIP to "0",
                        ItemAttribute.SLEEVE_LENGTH to "3",
                        ItemAttribute.SHOULDER_WIDTH to "4",
                        ItemAttribute.NECK_SIZE to "5",
                        ItemAttribute.INSEAM to "0",
                        ItemAttribute.RISE to "0",
                        ItemAttribute.LEG_OPENING to "0",
                        ItemAttribute.KNEE to "0",
                        ItemAttribute.THIGH to "0",
                        ItemAttribute.HEAD_CIRCUMFERENCE to "0"
                    ),
                    tags = listOf("tag")
                )
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun onTextChanged() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("type") } returns "OUTERWEAR"
        every { savedStateHandle.get<String>("id") } returns ""

        val viewModel = AddItemViewModel(savedStateHandle, itemRepository, thumbnailRepository)
        val results = mutableListOf<AddItemUiState>()
        backgroundScope.launch(testDispatcher) { viewModel.state.toList(results) }

        viewModel.onTextChanged(ItemAttribute.MATERIAL, "material")
        viewModel.onTextChanged(ItemAttribute.SLEEVE_LENGTH, "1.1")
        viewModel.onTextChanged(ItemAttribute.KNEE, "2")
        viewModel.onTextChanged(ItemAttribute.SIZE, "size")

        try {
            assertThat(results).hasSize(5)
            assertThat(results[0]).isEqualTo(AddItemUiState.Loaded(type = ItemType.OUTERWEAR))
            assertThat(results[1]).isEqualTo(
                AddItemUiState.Loaded(
                    type = ItemType.OUTERWEAR,
                    values = mapOf(
                        ItemAttribute.MATERIAL to "material",
                    )
                )
            )
            assertThat(results[2]).isEqualTo(
                AddItemUiState.Loaded(
                    type = ItemType.OUTERWEAR,
                    values = mapOf(
                        ItemAttribute.MATERIAL to "material",
                        ItemAttribute.SLEEVE_LENGTH to "1.1",
                    )
                )
            )
            assertThat(results[3]).isEqualTo(
                AddItemUiState.Loaded(
                    type = ItemType.OUTERWEAR,
                    values = mapOf(
                        ItemAttribute.MATERIAL to "material",
                        ItemAttribute.SLEEVE_LENGTH to "1.1",
                        ItemAttribute.KNEE to "2"
                    )
                )
            )
            assertThat(results[4]).isEqualTo(
                AddItemUiState.Loaded(
                    type = ItemType.OUTERWEAR,
                    values = mapOf(
                        ItemAttribute.MATERIAL to "material",
                        ItemAttribute.SIZE to "size",
                        ItemAttribute.SLEEVE_LENGTH to "1.1",
                        ItemAttribute.KNEE to "2"
                    )
                )
            )
        } finally {
            Dispatchers.resetMain()
        }
    }
}

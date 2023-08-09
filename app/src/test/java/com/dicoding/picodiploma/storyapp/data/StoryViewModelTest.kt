package com.dicoding.picodiploma.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.picodiploma.storyapp.adapter.StoryListAdapter
import com.dicoding.picodiploma.storyapp.api.response.ListStoryItem
import com.dicoding.picodiploma.storyapp.repository.AuthRepositoryInterface
import com.dicoding.picodiploma.storyapp.repository.MockAuthRepository
import com.dicoding.picodiploma.storyapp.repository.StoryRepository
import com.dicoding.picodiploma.storyapp.MainDispatcherRule
import com.dicoding.picodiploma.storyapp.ui.main.StoryViewModel
import com.dicoding.picodiploma.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var authRepository: AuthRepositoryInterface

    @Before
    fun setup() {
        authRepository = MockAuthRepository()
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoriesEntity()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUNZZVUyb2VsWFhCcDlLSVQiLCJpYXQiOjE2ODc5ODE2MTh9.uCAX6bQqbZVk9eYdeVtlF4sZ-OEYrvDMehsA1ZlLejU"))
            .thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository, authRepository)
        val actualStory: PagingData<ListStoryItem> = storyViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.STORY_COMPARATOR,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory(authRepository.getToken().toString())).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository, authRepository)
        val actualStory: PagingData<ListStoryItem> = storyViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.STORY_COMPARATOR,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}


val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) { }
    override fun onRemoved(position: Int, count: Int) { }
    override fun onMoved(fromPosition: Int, toPosition: Int) { }
    override fun onChanged(position: Int, count: Int, payload: Any?) { }

}
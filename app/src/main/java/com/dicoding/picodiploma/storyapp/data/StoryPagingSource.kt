package com.dicoding.picodiploma.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.storyapp.api.ApiService
import com.dicoding.picodiploma.storyapp.api.response.ListStoryItem
import okio.IOException
import retrofit2.HttpException

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getPage(token, position, params.loadSize)
            val story = responseData.listStory

            LoadResult.Page(
                data = story,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (story.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}
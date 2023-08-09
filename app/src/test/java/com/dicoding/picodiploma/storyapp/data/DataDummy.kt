package com.dicoding.picodiploma.storyapp.data

import com.dicoding.picodiploma.storyapp.api.response.ListStoryItem

object DataDummy {
    fun generateDummyStoriesEntity(): List<ListStoryItem> {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "url $i",
                "bali $i",
                "abdul$i",
                "umur aku $i tahun",
                i.toDouble(),
                "aaaa $i",
                i.toDouble()
            )
            storyList.add(story)
        }
        return storyList
    }
}
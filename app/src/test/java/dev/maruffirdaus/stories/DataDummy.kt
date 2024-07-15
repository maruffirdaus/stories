package dev.maruffirdaus.stories

import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity
import dev.maruffirdaus.stories.data.source.remote.response.GetResponse

object DataDummy {
    fun generateDummyGetResponse(): GetResponse {
        val items: MutableList<StoryEntity> = arrayListOf()

        for (i in 0..50) {
            val story = StoryEntity(
                i.toString(),
                "name $i",
                "description $i",
                "https://photo-url-$i",
                "$i"
            )
            items.add(story)
        }

        return GetResponse(
            false,
            "Stories fetched successfully",
            items
        )
    }
}
package dev.maruffirdaus.stories.data.source.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(listStory: List<StoryEntity>)

    @Query ("DELETE FROM story")
    suspend fun deleteAll()
}
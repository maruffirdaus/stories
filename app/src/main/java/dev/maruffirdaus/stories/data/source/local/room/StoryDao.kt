package dev.maruffirdaus.stories.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.maruffirdaus.stories.data.source.local.entity.StoryEntity

@Dao
interface StoryDao {
    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getStories(): LiveData<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStories(listStory: List<StoryEntity>)

    @Update
    fun updateStory(story: StoryEntity)

    @Query ("DELETE FROM story")
    fun deleteAll()
}
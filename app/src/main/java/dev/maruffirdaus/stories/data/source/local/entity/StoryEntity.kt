package dev.maruffirdaus.stories.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
    @field:ColumnInfo("id")
    @field:PrimaryKey
    val id: String,

    @field:ColumnInfo("name")
    val name: String,

    @field:ColumnInfo("description")
    val description: String,

    @field:ColumnInfo("photoUrl")
    val photoUrl: String,

    @field:ColumnInfo("createdAt")
    val createdAt: String,

    @field:ColumnInfo("lat")
    val lat: Double? = null,

    @field:ColumnInfo("lon")
    val lon: Double? = null
) : Parcelable
package dev.maruffirdaus.stories.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "story")
data class StoryEntity(
    @field:ColumnInfo("id")
    @field:PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:ColumnInfo("name")
    @field:SerializedName("name")
    val name: String,

    @field:ColumnInfo("description")
    @field:SerializedName("description")
    val description: String,

    @field:ColumnInfo("photoUrl")
    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:ColumnInfo("createdAt")
    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:ColumnInfo("lat")
    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:ColumnInfo("lon")
    @field:SerializedName("lon")
    val lon: Double? = null
) : Parcelable
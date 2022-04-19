package com.photogallery.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.photogallery.model.remote.PhotoItem

@Entity(tableName = "Photo")
data class PhotoEntity
    (
    @PrimaryKey var id: String,
    var height: Int,
    var url: String,
    var width: Int,
)

fun photoItemToPhotoEntity(photoItem: PhotoItem): PhotoEntity {
    return PhotoEntity(photoItem.id, photoItem.height, photoItem.url, photoItem.width)
}
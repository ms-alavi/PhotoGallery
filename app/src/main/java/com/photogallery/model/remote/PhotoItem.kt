package com.photogallery.model.remote

data class PhotoItem(
    val breeds: List<Any>,
    val height: Int,
    val id: String,
    val url: String,
    val width: Int,
)
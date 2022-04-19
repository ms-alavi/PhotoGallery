package com.photogallery.model.remote


import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("images/search?limit=10&order=DESC&x-api-key=d7d0baf8-af1e-4068-9b3c-47b6accfba0f")
    fun getPhotos(@Query("page") page: Int): Single<PhotoList>

    @GET("images/{image_id}?&x-api-key=d7d0baf8-af1e-4068-9b3c-47b6accfba0f")
    fun getPhotoDetail(@Path("image_id") id: String): Single<PhotoItem>
}
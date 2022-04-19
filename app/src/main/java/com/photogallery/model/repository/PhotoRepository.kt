package com.photogallery.model.repository

import com.photogallery.model.remote.ApiService
import com.photogallery.model.remote.PhotoList
import com.photogallery.model.remote.PhotoItem
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Single
import javax.inject.Inject

@ActivityRetainedScoped
class PhotoRepository @Inject constructor(private val apiService: ApiService) {
    fun getPhoto(page: Int): Single<PhotoList> = apiService.getPhotos(page)
    fun getPhotoDetail(id: String): Single<PhotoItem> = apiService.getPhotoDetail(id)
}
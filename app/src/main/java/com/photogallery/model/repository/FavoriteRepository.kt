package com.photogallery.model.repository

import com.photogallery.model.local.AppDataBase
import com.photogallery.model.local.PhotoEntity
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class FavoriteRepository @Inject constructor(private val database: AppDataBase) {
    fun getFavoritePhoto(photoId: String): Single<PhotoEntity?> =
        database.photoDao.loadOneByPhotoId(photoId)
    fun deleteFavoritePhoto(photo: PhotoEntity): Completable = database.photoDao.delete(photo.id)
    fun addFavoritePhoto(photo: PhotoEntity): Completable = database.photoDao.insert(photo)
    fun getAllFavorites(): Single<MutableList<PhotoEntity>> = database.photoDao.loadAll()
}
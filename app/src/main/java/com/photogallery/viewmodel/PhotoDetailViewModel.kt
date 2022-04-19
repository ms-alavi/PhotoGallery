package com.photogallery.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.photogallery.model.local.PhotoEntity
import com.photogallery.model.remote.PhotoItem
import com.photogallery.model.local.photoItemToPhotoEntity
import com.photogallery.model.repository.FavoriteRepository
import com.photogallery.model.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val photoRepo: PhotoRepository,
    private val favoriteRepo: FavoriteRepository
) : PhotoGalleryViewModel() {
    private val mTAG = "PhotoDetailViewModel"
    private var photoId: String? = null
    private val _photoDetail: MutableLiveData<PhotoItem> = MutableLiveData()
    val photoDetail: LiveData<PhotoItem> = _photoDetail
    private val _isFavorite: MutableLiveData<Boolean> = MutableLiveData()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private fun getPhotoDetail() {
        photoId?.let {
            photoRepo.getPhotoDetail(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<PhotoItem> {
                    override fun onSubscribe(disposable: Disposable) {
                        compositeDisposable.add(disposable)
                    }

                    override fun onSuccess(item: PhotoItem) {
                        _photoDetail.value = item
                    }

                    override fun onError(throwable: Throwable) {
                        Log.e(mTAG, throwable.message.toString())
                    }
                })
        }
    }

    fun updateFavoriteStatus() {
        if (isFavorite.value == true) {
            _photoDetail.value?.let { photoItemToPhotoEntity(it) }?.let {
                favoriteRepo.deleteFavoritePhoto(it)
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(disposable: Disposable) {
                            compositeDisposable.add(disposable)
                        }

                        override fun onComplete() {
                            Log.d(mTAG, "onComplete -> deletePhoto")
                        }

                        override fun onError(throwable: Throwable) {
                            Log.e(mTAG, throwable.message.toString())
                        }
                    })
            }
        } else {
            _photoDetail.value?.let { photoItemToPhotoEntity(it) }?.let {
                favoriteRepo.addFavoritePhoto(it)
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : CompletableObserver {
                        override fun onSubscribe(disposable: Disposable) {
                            compositeDisposable.add(disposable)
                        }

                        override fun onComplete() {
                            Log.d(mTAG, "onComplete -> addPhoto")
                        }

                        override fun onError(throwable: Throwable) {
                            Log.e(mTAG, throwable.message.toString())
                        }
                    })
            }
        }
        isFavorite.value?.let {
            _isFavorite.value = !it
        }
    }

    fun checkFavoriteStatus(photoId: String) {
        favoriteRepo.getFavoritePhoto(photoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PhotoEntity?> {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable.add(disposable)
                }

                override fun onSuccess(entity: PhotoEntity) {
                    _isFavorite.value = true
                }

                override fun onError(throwable: Throwable) {
                    _isFavorite.value = false
                    Log.e(mTAG, throwable.message.toString())
                }
            })
    }

    fun savePhotoId(id: String?) {
        photoId = id
        getPhotoDetail()
    }
}
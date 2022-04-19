package com.photogallery.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.photogallery.model.local.PhotoEntity
import com.photogallery.model.remote.PhotoItem
import com.photogallery.model.remote.PhotoList
import com.photogallery.model.repository.FavoriteRepository
import com.photogallery.model.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoRepo: PhotoRepository,
    private val favoriteRepo:FavoriteRepository
) : PhotoGalleryViewModel() {
    private val mTAG="HomeViewModel"
    private val _firstPageList: MutableLiveData<MutableList<PhotoItem>> = MutableLiveData()
    val firstPageList: LiveData<MutableList<PhotoItem>> = _firstPageList
    private val _nextPageList: MutableLiveData<PhotoList> = MutableLiveData()
    val nextPageList: LiveData<PhotoList> = _nextPageList
    private val _isFavoriteListShown: MutableLiveData<Boolean> = MutableLiveData(false)
    val isFavoriteListShown: LiveData<Boolean> = _isFavoriteListShown
    private val _favoriteList: MutableLiveData<List<PhotoEntity>> = MutableLiveData()
    val favoriteList: LiveData<List<PhotoEntity>> = _favoriteList
    private val _pageState: MutableLiveData<Int> = MutableLiveData()
    val pageState: LiveData<Int> = _pageState


    init {
        photoRepo.getPhoto(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PhotoList> {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable.add(disposable)
                }

                override fun onSuccess(list: PhotoList) {
                    _firstPageList.value = list
                }

                override fun onError(throwable: Throwable) {
                    Log.e(mTAG,throwable.message.toString())
                }
            })
    }

    fun getPhotosRemote(page: Int) {
        photoRepo.getPhoto(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PhotoList> {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable.add(disposable)
                }

                override fun onSuccess(list: PhotoList) {
                    _nextPageList.value = list
                }

                override fun onError(throwable: Throwable) {
                    Log.e(mTAG,throwable.message.toString())
                }
            })
    }

    fun getFavorites() {
         favoriteRepo.getAllFavorites().subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object :SingleObserver<List<PhotoEntity>>{
                 override fun onSubscribe(disposable: Disposable) {
                     compositeDisposable.add(disposable)
                 }

                 override fun onSuccess(list: List<PhotoEntity>) {
                     _favoriteList.value=list
                 }

                 override fun onError(throwable: Throwable) {
                     Log.e(mTAG,throwable.message.toString())
                 }


             })
    }

    fun setPageState(page:Int){
        _pageState.value=page
    }

    fun changeFavoritesVisibilityState(){
        _isFavoriteListShown.value = !_isFavoriteListShown.value!!
    }
}
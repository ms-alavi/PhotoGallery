package com.photogallery.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.photogallery.R
import com.photogallery.databinding.FragmentHomeBinding
import com.photogallery.view.adapter.PhotoGalleryAdapter
import com.photogallery.view.adapter.PhotoGalleryFavoriteAdapter
import com.photogallery.view.util.PaginationScrollListener
import com.photogallery.viewmodel.HomeViewModel


class HomeFragment : Fragment() {
    private val pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var currentPage: Int = pageStart
    private val favoriteAdapter = PhotoGalleryFavoriteAdapter()
    private lateinit var favoriteItem: MenuItem
    private lateinit var mBinding: FragmentHomeBinding
    private val mPhotoGalleryHomeViewModel: HomeViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private val adapter = PhotoGalleryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        favoriteItem = menu.findItem(R.id.show_favorite)
        mPhotoGalleryHomeViewModel.isFavoriteListShown.observe(viewLifecycleOwner) { isShow ->
            favoriteItem.setIcon(
                if (isShow) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
            )
            if (isShow) {
                mPhotoGalleryHomeViewModel.favoriteList.observe(viewLifecycleOwner) {
                    favoriteAdapter.setData(it)
                    mBinding.recyclerViewPhotoGalleryFavorite.visibility = View.VISIBLE
                    mBinding.recyclerViewPhotoGallery.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Used two recycler view in this fragment one for all pictures, one for favorite pictures
     * These two recycler view handled by visible and gone
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_favorite -> {
                mPhotoGalleryHomeViewModel.changeFavoritesVisibilityState()

                if (mPhotoGalleryHomeViewModel.isFavoriteListShown.value == true) {
                    mPhotoGalleryHomeViewModel.getFavorites()
                    mPhotoGalleryHomeViewModel.favoriteList.observe(viewLifecycleOwner) {
                        favoriteAdapter.setData(it)
                        mBinding.recyclerViewPhotoGalleryFavorite.visibility = View.VISIBLE
                        mBinding.recyclerViewPhotoGallery.visibility = View.GONE
                    }
                } else {
                    mBinding.recyclerViewPhotoGalleryFavorite.visibility = View.GONE
                    mBinding.recyclerViewPhotoGallery.visibility = View.VISIBLE
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        fetchData()
    }

    private fun init() {
        val linearLayoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )
        mBinding.recyclerViewPhotoGallery.layoutManager = linearLayoutManager
        mBinding.recyclerViewPhotoGallery.adapter = adapter
        mBinding.recyclerViewPhotoGalleryFavorite.layoutManager = GridLayoutManager(
            context,
            2
        )
        adapter.setOnClickHandler(object :PhotoGalleryAdapter.OnClickHandler{
            override fun setPageState() {
                mPhotoGalleryHomeViewModel.setPageState(currentPage)
            }
        })
        mBinding.recyclerViewPhotoGalleryFavorite.adapter =favoriteAdapter

        mBinding.recyclerViewPhotoGallery.addOnScrollListener(object :
            PaginationScrollListener(mBinding.recyclerViewPhotoGallery.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1
                Log.d("page",currentPage.toString())
                mPhotoGalleryHomeViewModel.getPhotosRemote(currentPage)
            }
            override fun isLastPage(): Boolean {
                return isLastPage
            }
            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }
    private fun fetchData() {
        mPhotoGalleryHomeViewModel.firstPageList.observe(viewLifecycleOwner) {
            mBinding.progressbar.visibility = View.GONE
            if (mPhotoGalleryHomeViewModel.pageState.value!=null){
                currentPage= mPhotoGalleryHomeViewModel.pageState.value!!
            }else{
                adapter.addAll(it)

            }
            adapter.addLoadingFooter()
        }
        mPhotoGalleryHomeViewModel.nextPageList.observe(viewLifecycleOwner) {
            adapter.removeLoadingFooter()
            isLoading = false
            adapter.addAll(it)
            adapter.addLoadingFooter()
        }
    }
}
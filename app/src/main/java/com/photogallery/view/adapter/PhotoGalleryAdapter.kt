package com.photogallery.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.photogallery.R
import com.photogallery.databinding.ListItemLoadingBinding
import com.photogallery.databinding.ListItemPhotoGalleryBinding
import com.photogallery.model.remote.PhotoItem
import com.squareup.picasso.Picasso


class PhotoGalleryAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData: MutableList<PhotoItem> = mutableListOf()
    private val loading = 0
    private val item = 1
    private var isLoadingAdded = false
    private var mOnClickHandler: OnClickHandler? = null

    fun setOnClickHandler(onClickHandler: OnClickHandler) {
        mOnClickHandler = onClickHandler
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null

        when (viewType) {
            item -> {
                val binding: ListItemPhotoGalleryBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_photo_gallery, parent, false
                )
                viewHolder = PhotoGalleryViewHolder(binding)
            }
            loading -> {
                val binding: ListItemLoadingBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.list_item_loading, parent, false
                )
                viewHolder = LoadingVH(binding)
            }
        }
        return viewHolder!!
    }


    fun addAll(data: MutableList<PhotoItem>) {
        for (photo in data) {
            add(photo)
        }
    }

    private fun add(photo: PhotoItem) {
        mData.add(photo)
        notifyItemInserted(mData.size - 1)
    }

    inner class PhotoGalleryViewHolder(private var mBinding: ListItemPhotoGalleryBinding) :
        RecyclerView.ViewHolder(
            mBinding.root
        ) {
        fun bind(photo: PhotoItem) {
            mBinding.root.setOnClickListener {
                mOnClickHandler?.setPageState()
                val bundle = bundleOf("id" to photo.id)
                mBinding.root.findNavController()
                    .navigate(R.id.action_homeFragment_to_detailFragment2, bundle)
            }
            if (photo.url.trim() != "") {
                Picasso.get()
                    .load(photo.url)
                    .placeholder(R.mipmap.ic_place_holder)
                    .into(mBinding.itemImageView)
            } else {
                mBinding.itemImageView.setImageResource(R.mipmap.ic_place_holder)
            }
        }
    }


    class LoadingVH(private var binding: ListItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.loadmoreProgress.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mData.size - 1 && isLoadingAdded) loading else item
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(PhotoItem(mutableListOf(), 0, "", "", 0))
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = mData.size - 1
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photo = mData[position]
        when (getItemViewType(position)) {
            item -> {
                val photoViewHolder: PhotoGalleryViewHolder = holder as PhotoGalleryViewHolder
                photo.let { photoViewHolder.bind(it) }
            }
            loading -> {
                val loadingVH: LoadingVH = holder as LoadingVH
                loadingVH.bind()
            }

        }

    }

    interface OnClickHandler {
        fun setPageState()
    }

}
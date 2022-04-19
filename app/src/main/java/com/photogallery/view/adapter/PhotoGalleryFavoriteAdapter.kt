package com.photogallery.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.photogallery.R
import com.photogallery.databinding.ListItemPhotoGalleryBinding
import com.photogallery.model.local.PhotoEntity
import com.squareup.picasso.Picasso

class PhotoGalleryFavoriteAdapter :
    RecyclerView.Adapter<PhotoGalleryFavoriteAdapter.ViewHolder>() {
    var set: Set<String> = HashSet()
    private var mData: List<PhotoEntity>? = null

    fun setData(data: List<PhotoEntity>?) {
        mData = data
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding: ListItemPhotoGalleryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_photo_gallery, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = mData?.get(position)
        photo?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return mData!!.size
    }

    inner class ViewHolder(private var mBinding: ListItemPhotoGalleryBinding) :
        RecyclerView.ViewHolder(
            mBinding.root
        ) {
        fun bind(photo: PhotoEntity) {
            mBinding.root.setOnClickListener {
                val bundle = bundleOf("id" to photo.id)
                mBinding.root.findNavController()
                    .navigate(R.id.action_homeFragment_to_detailFragment2, bundle)
            }
            Picasso.get()
                .load(photo.url)
                .placeholder(R.mipmap.ic_place_holder)
                .into(mBinding.itemImageView)
        }
    }

}
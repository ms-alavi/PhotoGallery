package com.photogallery.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.photogallery.R
import com.photogallery.databinding.FragmentDetailBinding
import com.photogallery.viewmodel.PhotoDetailViewModel
import com.squareup.picasso.Picasso


class DetailFragment : Fragment() {
    private lateinit var mBinding: FragmentDetailBinding
    private var photoId: String = ""
    private lateinit var item: MenuItem

    private val photoGalleryViewModel: PhotoDetailViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            photoId = requireArguments().getString("id", "")
            photoGalleryViewModel.savePhotoId(photoId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(photoGalleryViewModel) {
            checkFavoriteStatus(photoId)
            photoDetail.observe(
                viewLifecycleOwner,
            ) {
                Picasso.get()
                    .load(it.url)
                    .placeholder(R.mipmap.ic_place_holder)
                    .into(mBinding.img)
                mBinding.detailTitleTextView.text = "height:${it.height},width:${it.width}"
            }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detial, menu)
        item = menu.findItem(R.id.add_favorite)
        photoGalleryViewModel.isFavorite.observe(
            viewLifecycleOwner,
        ) {
            it?.let {
                item.setIcon(
                    if (it) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
            }
        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun shareImageIntent(uri: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, uri)
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.send_photo))
        /**
         * we prevent app from crash if the intent has no destination
         */
        if (sendIntent.resolveActivity(requireActivity().packageManager) != null) startActivity(
            shareIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_favorite -> {
                photoGalleryViewModel.updateFavoriteStatus()
                true
            }
            R.id.share -> {
                photoGalleryViewModel.photoDetail.value?.let { shareImageIntent(it.url) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
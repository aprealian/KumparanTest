package me.aprilian.kumparantest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import me.aprilian.kumparantest.data.Photo
import me.aprilian.kumparantest.databinding.DialogPhotoViewerBinding
import me.aprilian.kumparantest.utils.extension.load

const val ARG_PHOTO = "photo"

class PhotoViewerDialog : DialogFragment() {

    private lateinit var binding: DialogPhotoViewerBinding
    private var photo: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        photo = arguments?.getParcelable<Photo?>(ARG_PHOTO)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogPhotoViewerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photo?.let {
            binding.tvTitle.text = it.title
            binding.ivPhoto.load(it.url)
        }
    }

    companion object {
        fun newInstance(photo: Photo): PhotoViewerDialog {
            val dialog = PhotoViewerDialog()
            val args = Bundle()
            args.putParcelable(ARG_PHOTO, photo)
            dialog.arguments = args

            return dialog
        }
    }
}
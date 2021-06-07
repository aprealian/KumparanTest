package me.aprilian.kumparantest.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.data.Photo
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.databinding.ItemPhotoBinding
import me.aprilian.kumparantest.ui.base.BaseRVAdapter
import me.aprilian.kumparantest.utils.Utils.toast
import me.aprilian.kumparantest.utils.extension.load

class PhotoAdapter(ctx: Context?, resource: Resource<List<Photo>>, private val clickListener: (Photo?) -> Unit) : BaseRVAdapter<Photo>(ctx, resource) {

    private val maxPhotos = 6

    data class PhotoState (
            val isLatestCounter: Boolean,
            val moreItem: Int,
    )

    inner class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(photo: Photo?, position: Int) {
            photo?.let {
                val isLatestCounter = (position >= maxPhotos-1)
                val moreCounter = (resource.data?.size ?: 0) - (maxPhotos-1)
                val photoState = PhotoState(isLatestCounter, moreCounter)

                binding.photo = it
                binding.photoState = photoState
                binding.ivPhoto.load(it.thumbnailUrl)
                itemView.setOnClickListener {
                    if (!isLatestCounter) clickListener(photo) else binding.root.context?.toast(binding.root.context?.getString(R.string.coming_soon))
                }
                binding.executePendingBindings()
            }
        }
    }

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return PhotoViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PhotoViewHolder) holder.bind(resource.data?.get(position), position)
    }

    override fun getItemCount(): Int {
        return if (resource.data?.size ?: 0 > maxPhotos) maxPhotos else resource.data?.size ?: 0
    }
}
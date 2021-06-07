package me.aprilian.kumparantest.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.data.Album
import me.aprilian.kumparantest.data.Photo
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.databinding.ItemAlbumBinding
import me.aprilian.kumparantest.databinding.ItemPhotoBinding
import me.aprilian.kumparantest.ui.base.BaseRVAdapter
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils
import me.aprilian.kumparantest.utils.Utils.toast
import me.aprilian.kumparantest.utils.extension.load

class AlbumAdapter(ctx: Context?, resource: Resource<List<Album>>, private val clickListener: (Photo?) -> Unit) : BaseRVAdapter<Album>(ctx, resource) {

    inner class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album?){
            if (album == null) return

            //set photo adapter
            val adapter = PhotoAdapter(ctx, Resource(Resource.Status.SUCCESS, album.photos, ""), clickListener)

            //set binding
            binding.also {
                it.album = album
                it.adapter = adapter
                it.rvPhoto.addItemDecoration(SpacesItemDecoration(Utils.dpToPx(binding.root.context,16f).toInt()))
                it.executePendingBindings()
            }
        }
    }

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return AlbumViewHolder(ItemAlbumBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlbumViewHolder) {
            holder.bind(resource.data?.get(position))
        }
    }
}
package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Album
import me.aprilian.kumparantest.data.Photo
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentUserBinding
import me.aprilian.kumparantest.databinding.ItemAlbumBinding
import me.aprilian.kumparantest.databinding.ItemPhotoBinding
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.extension.load
import javax.inject.Inject

const val ARG_USER = "user"

@AndroidEntryPoint
class UserFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding
    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initAdapter()
        initObservers()
    }

    private fun initAdapter() {
        adapter = AlbumAdapter()
        binding.adapter = adapter
        userViewModel.getAlbums().let { adapter.submitList(it) }
        adapter.setListener(object: IPhoto{
            override fun onClickPhoto(photo: Photo) {
                openPhoto(photo)
            }
        })
    }

    private fun initObservers() {
        userViewModel.isRefreshList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })
    }

    private fun initArgs() {
        arguments?.let { bundle ->
            userViewModel.user = bundle.getParcelable(ARG_USER) as User?
            userViewModel.user?.let {
                binding.user = it
                userViewModel.loadAlbums(it.id)
            }
        }
    }

    private fun openPhoto(photo: Photo){
        val bottomSheetDialog: PhotoViewerDialog = PhotoViewerDialog.newInstance(photo)
        bottomSheetDialog.show(childFragmentManager, "Photo Viewer Dialog")
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
): ViewModel(){
    var user: User? = null

    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()

    private val albums: ArrayList<Album> = arrayListOf()

    fun getAlbums(): ArrayList<Album> {
        return albums
    }

    fun loadAlbums(userId: Int?) = viewModelScope.launch {
        userId ?: return@launch
        userRepository.getUserAlbums(userId).let {
            val result = it.body()
            if (it.isSuccessful && result != null){
                for (album in result){
                    getAlbumPhotos(album.id)?.let { list ->
                        album.photos = list
                    }
                    albums.add(album)
                }
                isRefreshList.value = true
            }
        }
    }

    private suspend fun getAlbumPhotos(albumId: Int): ArrayList<Photo>? {
        return userRepository.getPhotos(albumId).body()
    }
}

class AlbumAdapter : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(Companion) {

    private var listener: IPhoto? = null

    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAlbumBinding.inflate(layoutInflater)

        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val currentAlbums = getItem(position)
        holder.binding.album = currentAlbums
        holder.binding.executePendingBindings()
        holder.binding.rvPhoto.addItemDecoration(SpacesItemDecoration(16))

        //set photo adapter
        val adapter = PhotoAdapter()
        adapter.setListener(listener)
        holder.binding.adapter = adapter
        adapter.submitList(currentAlbums.photos)
        //Photo.getSamples().let { adapter.submitList(it) }//testing
    }

    fun setListener(listener: IPhoto?){
        this.listener = listener
    }
}

class PhotoAdapter : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(Companion) {

    private var listener: IPhoto? = null

    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(layoutInflater)

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentPhoto = getItem(position)
        holder.binding.photo = currentPhoto
        holder.binding.ivPhoto.load(currentPhoto.thumbnailUrl)
        holder.binding.executePendingBindings()
        holder.itemView.setOnClickListener {
            listener?.onClickPhoto(currentPhoto)
        }
    }

    fun setListener(listener: IPhoto?){
        this.listener = listener
    }
}

interface IPhoto{
    fun onClickPhoto(photo: Photo)
}
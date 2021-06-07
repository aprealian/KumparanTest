package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.data.*
import me.aprilian.kumparantest.databinding.FragmentUserBinding
import me.aprilian.kumparantest.databinding.ItemAlbumBinding
import me.aprilian.kumparantest.databinding.ItemPhotoBinding
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.ui.base.BaseFragment
import me.aprilian.kumparantest.ui.base.BaseRVAdapter
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils.dpToPx
import me.aprilian.kumparantest.utils.Utils.toast
import me.aprilian.kumparantest.utils.extension.load
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : BaseFragment() {

    private val args: UserFragmentArgs by navArgs()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding
    private lateinit var adapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.userId = args.userId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = userViewModel
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initObservers()
        loadData()
    }

    private fun initAdapter() {
        adapter = AlbumAdapter(requireContext(), Resource.loading(null)) {
            openPhoto(it)
        }
        binding.adapter = adapter
    }

    private fun openPhoto(photo: Photo?){
        if (photo == null) return
        val bottomSheetDialog: PhotoViewerDialog = PhotoViewerDialog.newInstance(photo)
        bottomSheetDialog.show(childFragmentManager, "Photo Viewer Dialog")
    }

    private fun initObservers() {
        userViewModel.albums.observe(viewLifecycleOwner, Observer {
            updateAlbumsData(it)
        })
    }

    private fun updateAlbumsData(data: Resource<List<Album>>?) {
        if (binding.rvAlbums.adapter == null || data == null) return
        (binding.rvAlbums.adapter as AlbumAdapter).submitData(data)
    }

    private fun loadData() {
        userViewModel.getUser()
        userViewModel.getAlbums()
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel(){
    var userId: Int? = null

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    fun getUser(){
        viewModelScope.launch {
            userId?.let {
                _user.value = userRepository.getUser(it)
                getUser()
            }
        }
    }

    private val _albums = MutableLiveData<Resource<List<Album>>>()
    val albums: LiveData<Resource<List<Album>>> = _albums

    fun getAlbums(){
        viewModelScope.launch {
            userId?.let {
                val albumsResult = userRepository.getUserAlbums(it)
                albumsResult.data?.let { list ->
                    for (album in list){
                        getAlbumPhotos(album.id)?.let { it ->
                            album.photos = it
                        }
                    }
                    _albums.value = Resource(Resource.Status.SUCCESS, list, "")
                }
            } ?: run { _albums.value = Resource(Resource.Status.EMPTY, arrayListOf(), "") }
        }
    }

    private suspend fun getAlbumPhotos(albumId: Int): ArrayList<Photo>? {
        return userRepository.getPhotos(albumId).data
    }
}

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
                it.rvPhoto.addItemDecoration(SpacesItemDecoration(dpToPx(binding.root.context,16f).toInt()))
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
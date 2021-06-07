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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.api.Resource
import me.aprilian.kumparantest.api.Resource.Companion.getErrorMessageToUser
import me.aprilian.kumparantest.data.Album
import me.aprilian.kumparantest.data.Photo
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentUserBinding
import me.aprilian.kumparantest.databinding.ItemAlbumBinding
import me.aprilian.kumparantest.databinding.ItemPhotoBinding
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils.toast
import me.aprilian.kumparantest.utils.extension.load
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val args: UserFragmentArgs by navArgs()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding
    private lateinit var adapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.user = args.user
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = userViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initAdapter()
        initObservers()
        initListener()
        loadData()
    }

    private fun initViews() {
        userViewModel.user?.let {
            binding.user = it
        }
    }

    private fun initListener(){
        binding.buttonBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun initAdapter() {
        adapter = AlbumAdapter(requireContext())
        binding.adapter = adapter
        userViewModel.getAlbums().let { adapter.submitList(it) }
        adapter.setListener(object: IPhoto{
            override fun onClickPhoto(photo: Photo) {
                openPhoto(photo)
            }
        })
    }

    private fun openPhoto(photo: Photo){
        val bottomSheetDialog: PhotoViewerDialog = PhotoViewerDialog.newInstance(photo)
        bottomSheetDialog.show(childFragmentManager, "Photo Viewer Dialog")
    }

    private fun initObservers() {
        userViewModel.isRefreshList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        userViewModel.message.observe(viewLifecycleOwner, Observer { message ->
            requireContext().toast(message)
        })
    }

    private fun loadData() {
        userViewModel.user?.id?.let {
            userViewModel.loadAlbums(it)
        }
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
): ViewModel(){
    var user: User? = null

    private val albums: ArrayList<Album> = arrayListOf()
    val totalAlbums: MutableLiveData<Int> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isAlbumNotFound: MutableLiveData<Boolean> = MutableLiveData()
    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()

    fun getAlbums(): ArrayList<Album> {
        return albums
    }

    fun loadAlbums(userId: Int?) = viewModelScope.launch {
        userId ?: return@launch
        isLoading.value = true
        userRepository.getUserAlbums(userId).let {
            val result = it.data
            if (it.status == Resource.Status.SUCCESS && result != null){
                for (album in result){
                    getAlbumPhotos(album.id)?.let { list ->
                        album.photos = list
                    }
                    albums.add(album)
                }
                totalAlbums.value = albums.size
                isAlbumNotFound.value = albums.isNullOrEmpty()
                isRefreshList.value = true
            } else if (it.status == Resource.Status.ERROR) {
                message.value = getErrorMessageToUser(context, it.message)
            }
        }
        isLoading.value = false
    }

    private suspend fun getAlbumPhotos(albumId: Int): ArrayList<Photo>? {
        return userRepository.getPhotos(albumId).data
    }
}

class AlbumAdapter constructor(
    private val context: Context
) : ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(Companion) {

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
        adapter.submitList(currentAlbums.photos)
        adapter.setListener(listener)
        holder.binding.adapter = adapter
        holder.binding.rvPhoto.addItemDecoration(SpacesItemDecoration(ViewUtils.dpToPx(context,16).toInt()))
        //Photo.getSamples().let { adapter.submitList(it) }//testing
    }

    fun setListener(listener: IPhoto?){
        this.listener = listener
    }
}

class PhotoAdapter : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(Companion) {

    private var listener: IPhoto? = null
    private val maxPhotos = 6

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
        val isLatestCounter = (position >= maxPhotos-1)

        holder.binding.photo = currentPhoto
        holder.binding.ivPhoto.load(currentPhoto.thumbnailUrl)
        holder.itemView.setOnClickListener {
            val context = holder.binding.root.context
            if (!isLatestCounter) listener?.onClickPhoto(currentPhoto) else context.toast(context.getString(R.string.coming_soon))
        }

        //check is latest counter
        if (isLatestCounter){
            holder.binding.tvPhotosCounter.apply {
                val moreCounter = currentList.size-(maxPhotos-1)
                text = "+${moreCounter}"
                visibility = View.VISIBLE
            }
        } else {
            holder.binding.tvPhotosCounter.visibility = View.GONE
        }

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (currentList.size > maxPhotos) maxPhotos else currentList.size
    }

    fun setListener(listener: IPhoto?){
        this.listener = listener
    }
}

interface IPhoto{
    fun onClickPhoto(photo: Photo)
}
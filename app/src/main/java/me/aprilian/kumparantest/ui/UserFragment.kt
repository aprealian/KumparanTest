package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import me.aprilian.kumparantest.data.AlbumsResponse
import me.aprilian.kumparantest.data.PhotoResponse
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
        adapter = AlbumAdapter(requireActivity())
        binding.adapter = adapter
        userViewModel.getAlbums().let { adapter.submitList(it) }
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
}

@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
): ViewModel(){
    var user: User? = null

    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()

    private val albums: ArrayList<AlbumsResponse.AlbumsResponseItem> = arrayListOf()

    fun getAlbums(): ArrayList<AlbumsResponse.AlbumsResponseItem> {
        return albums
    }

    fun loadAlbums(userId: Int?) = viewModelScope.launch {
        userId ?: return@launch
        userRepository.getUserAlbums(userId).let {
            val result = it.body()
            if (it.isSuccessful && result != null){
                albums.addAll(result)
                isRefreshList.value = true
            }
            //context.toast("albums "+albums.size)
        }
    }
}

class AlbumAdapter constructor(
    private val activity: FragmentActivity
) : ListAdapter<AlbumsResponse.AlbumsResponseItem, AlbumAdapter.AlbumViewHolder>(Companion) {

    class AlbumViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<AlbumsResponse.AlbumsResponseItem>() {
        override fun areItemsTheSame(oldItem: AlbumsResponse.AlbumsResponseItem, newItem: AlbumsResponse.AlbumsResponseItem): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: AlbumsResponse.AlbumsResponseItem, newItem: AlbumsResponse.AlbumsResponseItem): Boolean = oldItem.id == newItem.id
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
        val adapter = PhotoAdapter(activity)
        holder.binding.adapter = adapter
        PhotoResponse.PhotoItem.getSamples().let { adapter.submitList(it) }
    }
}

class PhotoAdapter constructor(
    private val activity: FragmentActivity
) : ListAdapter<PhotoResponse.PhotoItem, PhotoAdapter.PhotoViewHolder>(Companion) {

//    private var listener: IPhoto? = null

    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<PhotoResponse.PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoResponse.PhotoItem, newItem: PhotoResponse.PhotoItem): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: PhotoResponse.PhotoItem, newItem: PhotoResponse.PhotoItem): Boolean = oldItem.id == newItem.id
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
//            listener?.onClickPhoto(currentPhoto)
            openPhoto(activity, currentPhoto)
        }
    }

//    fun setListener(listener: IPhoto){
//        this.listener = listener
//    }
//
//    interface IPhoto{
//        fun onClickPhoto(photo: PhotoResponse.PhotoItem)
//    }

    private fun openPhoto(activity: FragmentActivity, photo: PhotoResponse.PhotoItem){
        val bottomSheetDialog: PhotoViewerDialog = PhotoViewerDialog.newInstance(photo)
        bottomSheetDialog.show(activity.supportFragmentManager, "Bottom Sheet Dialog Fragment")
    }
}
package me.aprilian.kumparantest.ui.features.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import me.aprilian.kumparantest.data.*
import me.aprilian.kumparantest.databinding.FragmentUserBinding
import me.aprilian.kumparantest.ui.adapter.AlbumAdapter
import me.aprilian.kumparantest.ui.base.BaseFragment

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
        binding = FragmentUserBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = userViewModel
            it.fragment = this
            it.executePendingBindings()
        }
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
        userViewModel.loadUser()
    }
}
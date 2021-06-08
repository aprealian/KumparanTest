package me.aprilian.kumparantest.ui.features.postlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import me.aprilian.kumparantest.data.*
import me.aprilian.kumparantest.databinding.FragmentPostListBinding
import me.aprilian.kumparantest.ui.adapter.PostAdapter
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils.dpToPx

@AndroidEntryPoint
class PostListFragment : Fragment() {
    private val postListViewModel: PostListViewModel by viewModels()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostListBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = postListViewModel
            it.executePendingBindings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupObserver()
    }

    private fun setupObserver() {
        postListViewModel.posts.observe(viewLifecycleOwner, Observer {
            updateData(it)
        })
    }

    private fun updateData(data: Resource<List<Post>>?) {
        if (binding.rvPosts.adapter == null || data == null) return
        (binding.rvPosts.adapter as PostAdapter).submitData(data)
    }

    private fun setupAdapter(){
        adapter = PostAdapter(requireContext(), Resource.loading(null)) {
            openPost(it?.id)
        }
        binding.adapter = adapter
        binding.rvPosts.addItemDecoration(SpacesItemDecoration(dpToPx(requireContext(),16.0f).toInt()))
    }

    private fun openPost(postId: Int?) {
        if (postId == null) return
        val action = PostListFragmentDirections.actionPostListToPost()
        action.postId = postId
        view?.findNavController()?.navigate(action)
    }
}
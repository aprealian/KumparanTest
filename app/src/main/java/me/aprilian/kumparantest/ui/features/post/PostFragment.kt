package me.aprilian.kumparantest.ui.features.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentPostBinding
import me.aprilian.kumparantest.ui.adapter.CommentsAdapter
import me.aprilian.kumparantest.ui.base.BaseFragment
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils

@AndroidEntryPoint
class PostFragment : BaseFragment() {

    private val args: PostFragmentArgs by navArgs()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var binding: FragmentPostBinding
    private lateinit var adapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postViewModel.postId = args.postId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostBinding.inflate(layoutInflater).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.vm = postViewModel
            it.fragment = this
            it.executePendingBindings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupObserver()
        loadData()
    }

    private fun setupObserver() {
        postViewModel.comments.observe(viewLifecycleOwner, Observer {
            updateData(it)
        })
    }

    private fun updateData(data: Resource<List<Comment>>?) {
        if (binding.rvComments.adapter == null || data == null) return
        (binding.rvComments.adapter as CommentsAdapter).submitData(data)
    }

    private fun setupAdapter() {
        adapter = CommentsAdapter(requireContext(), Resource.loading(null))
        binding.adapter = adapter
        binding.rvComments.addItemDecoration(SpacesItemDecoration(Utils.dpToPx(requireContext(),16.0f).toInt()))
    }

    private fun loadData() {
        postViewModel.getPost()
        postViewModel.getComments()
    }

    fun openUser(view: View?, user: User?){
        if (view == null || user == null) return
        val action = PostFragmentDirections.actionPostToUser().apply { this.userId = user.id }
        view.findNavController().navigate(action)
    }
}
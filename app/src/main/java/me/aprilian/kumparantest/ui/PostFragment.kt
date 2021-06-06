package me.aprilian.kumparantest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.api.Resource
import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentPostBinding
import me.aprilian.kumparantest.databinding.ItemCommentBinding
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment() {

    private val args: PostFragmentArgs by navArgs()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var binding: FragmentPostBinding
    private lateinit var adapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postViewModel.post = args.post
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initAdapter()
        initObserver()
        initListener()
        loadComments()
    }

    private fun initListener() {
        binding.buttonBack.setOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun initAdapter() {
        adapter = CommentsAdapter()
        postViewModel.getComments().let { adapter.submitList(it) }
        binding.adapter = adapter
        binding.vm = postViewModel
        binding.rvComments.addItemDecoration(SpacesItemDecoration(Utils.dpToPx(requireContext(),16.0f).toInt()))
    }

    private fun initObserver() {
        postViewModel.isRefreshList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
            binding.tvCommentCount.text = "Comments (${adapter.itemCount})"
        })
    }

    private fun initViews() {
        postViewModel.post?.let {
            binding.post = it
        }
    }

    private fun loadComments() {
        if (postViewModel.getComments().isEmpty()){
            postViewModel.post?.id?.let {
                postViewModel.loadComments(it)
            }
        }
    }
}

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository
): ViewModel(){
    var post: Post? = null

    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()

    private val comments: ArrayList<Comment> = arrayListOf()

    init {
        post?.id?.let { loadComments(it) }
    }

    fun getComments(): ArrayList<Comment> {
        return comments
    }

    fun loadComments(postId: Int) = viewModelScope.launch {
        postRepository.getPostComments(postId).let {
            val result = it.data
            if (it.status == Resource.Status.SUCCESS && result != null){
                comments.addAll(result)
                isRefreshList.value = true
            }
        }
    }

    fun openUser(view: View, user: User){
        val action = PostFragmentDirections.actionPostToUser()
        action.user = user
        view.findNavController().navigate(action)
    }
}

class CommentsAdapter : ListAdapter<Comment, CommentsAdapter.CommentViewHolder>(Companion) {

    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater)

        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentComment = getItem(position)
        holder.binding.comment = currentComment
        holder.binding.executePendingBindings()
    }
}
package me.aprilian.kumparantest.ui

import android.content.Context
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentPostBinding
import me.aprilian.kumparantest.databinding.ItemCommentBinding
import me.aprilian.kumparantest.repository.PostRepository
import javax.inject.Inject

private const val ARG_POST = "post"

@AndroidEntryPoint
class PostFragment : Fragment() {

    //val args: PostListFragmentArgs by navArgs()
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var binding: FragmentPostBinding
    private lateinit var adapter: CommentsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArgs()
        initAdapter()
        initObserver()
    }

    private fun initAdapter() {
        adapter = CommentsAdapter()
        binding.adapter = adapter
        postViewModel.getComments().let { adapter.submitList(it) }
        binding.vm = postViewModel
    }

    private fun initObserver() {
        postViewModel.isRefreshList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })
    }

    private fun initArgs() {
        arguments?.let { bundle ->
            postViewModel.post = bundle.getParcelable(ARG_POST) as Post?
            postViewModel.post?.let {
                binding.post = it
                postViewModel.loadComments(it.id)
            }
        }
    }
}

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val postRepository: PostRepository
): ViewModel(){
    var post: Post? = null

    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()

    private val comments: ArrayList<Comment> = arrayListOf()

    fun getComments(): ArrayList<Comment> {
        return comments
    }

    fun loadComments(postId: Int) = viewModelScope.launch {
        postRepository.getPostComments(postId).let {
            val result = it.body()
            if (it.isSuccessful && result != null){
                comments.addAll(result)
                isRefreshList.value = true
            }
        }
    }

    fun openUser(view: View, user: User){
        val bundle = Bundle()
        bundle.putParcelable(ARG_USER, user)
        view.findNavController().navigate(R.id.action_post_to_user, bundle)
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
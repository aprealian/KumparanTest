package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentPostBinding
import me.aprilian.kumparantest.databinding.ItemCommentBinding
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.ui.base.BaseFragment
import me.aprilian.kumparantest.ui.base.BaseRVAdapter
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils
import javax.inject.Inject

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
        binding = FragmentPostBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = postViewModel
        binding.fragment = this
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

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
): ViewModel(){
    var postId: Int? = null

    private val _post = MutableLiveData<Resource<Post>>()
    val post: LiveData<Resource<Post>> = _post

    fun getPost(){
        viewModelScope.launch {
            postId?.let {
                _post.value = postRepository.getPost(it)
                getUser()
            }
        }
    }

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    private fun getUser(){
        viewModelScope.launch {
            post.value?.data?.userId?.let { _user.value = userRepository.getUser(it) }
        }
    }

    private val _comments = MutableLiveData<Resource<List<Comment>>>()
    val comments: LiveData<Resource<List<Comment>>> = _comments

    fun getComments(){
        viewModelScope.launch {
            postId?.let { _comments.value = postRepository.getPostComments(it) }
        }
    }
}

class CommentsAdapter(ctx: Context?, resource: Resource<List<Comment>>) : BaseRVAdapter<Comment>(ctx, resource) {

    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return CommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentViewHolder) {
            holder.binding.comment = resource.data?.get(position)
            holder.binding.executePendingBindings()
        }
    }
}
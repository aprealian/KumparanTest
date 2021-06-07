package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.dpToPx
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.api.Resource
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.databinding.FragmentPostListBinding
import me.aprilian.kumparantest.databinding.ItemPostBinding
import me.aprilian.kumparantest.databinding.ItemPostLoadingBinding
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils.toast
import javax.inject.Inject

@AndroidEntryPoint
class PostListFragment : Fragment() {
    private val postListViewModel: PostListViewModel by viewModels()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initObserver()
    }

    private fun initObserver() {
        postListViewModel.isRefreshList.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        postListViewModel.message.observe(viewLifecycleOwner, Observer { message ->
            requireContext().toast(message)
        })
    }

    private fun initAdapter(){
        adapter = PostAdapter()
        adapter.setListener(onClickPost)
        postListViewModel.getPosts().let { adapter.submitList(it) }
        binding.adapter = adapter
        binding.rvPosts.addItemDecoration(SpacesItemDecoration(dpToPx(requireContext(),16).toInt()))

        //restore list position state
        binding.rvPosts.layoutManager?.onRestoreInstanceState(postListViewModel.recyclerViewState)
    }

    private val onClickPost = object: PostAdapter.IPost{
        override fun clickPost(post: Post) {
            val action = PostListFragmentDirections.actionPostListToPost()
            action.post = post
            view?.findNavController()?.navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        //save recyclerview state and position
        postListViewModel.recyclerViewState = binding.rvPosts.layoutManager?.onSaveInstanceState()
    }
}

@HiltViewModel
class PostListViewModel @Inject constructor(
   @ApplicationContext private val context: Context,
   private val postRepository: PostRepository,
   private val userRepository: UserRepository
): ViewModel(){
    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()
    val message: MutableLiveData<String> = MutableLiveData()
    var recyclerViewState: Parcelable? = null //save recyclerView state

    private val posts: ArrayList<Post> = arrayListOf()

    fun getPosts(): ArrayList<Post> {
        return posts
    }

    private fun loadPosts() = viewModelScope.launch {
        showLoading()
        postRepository.getPosts(1, 10).let {
            when(it.status){
                Resource.Status.SUCCESS -> {
                    it.data?.let { list ->
                        val refreshEvery = 10
                        var counter = 0

                        for (post in list){
                            //getting user data
                            val user = getUser(post.userId)
                            post.user = user
                            //posts.add(post)
                            addItemBeforeLoadingItem(post)
                            counter++

                            //check condition to refresh list
                            if (counter >= refreshEvery){
                                isRefreshList.value = true
                                counter = 0
                            }
                        }

                        //save to cache
                        postRepository.savePosts(posts)

                        isRefreshList.value = true
                    }
                }
                else -> {
                    //API Error handling
                    if (Resource.isNoInternetConnection(it.message)){
                        //load from cache
                        postRepository.getPostsFromDb()?.let { list ->
                            posts.clear()
                            posts.addAll(list)
                        }
                        isRefreshList.value = true
                    }

                    message.value = Resource.getErrorMessageToUser(context, it.message)
                }
            }
        }
        hideLoading()
    }

    private fun addItemBeforeLoadingItem(item: Post){
        val index = posts.indexOfFirst { it.id == 0 }
        if (index >= 0) posts.add(index, item) else posts.add(item)
    }

    private fun showLoading(){
        posts.addAll(Post.createList(4))
        isRefreshList.value = true
    }

    private fun hideLoading(){
        posts.removeIf { it.id == 0 }
        isRefreshList.value = true
    }

    private suspend fun getUser(userId: Int): User? {
        return userRepository.getUser(userId).data
    }

    init {
        loadPosts()
    }
}

class PostAdapter : ListAdapter<Post, RecyclerView.ViewHolder>(Companion) {

    private var listener: IPost? = null
    private val TYPE_ITEM = 0
    private val TYPE_LOADING_ITEM = 1

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Post){
            binding.post = item
            binding.executePendingBindings()

            itemView.setOnClickListener {
                listener?.clickPost(item)
            }
        }
    }

    inner class LoadingViewHolder(val binding: ItemPostLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_ITEM -> {
                val binding = ItemPostBinding.inflate(layoutInflater)
                PostViewHolder(binding)
            }
            else -> {
                val binding = ItemPostLoadingBinding.inflate(layoutInflater)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = getItem(position)

        if (holder.itemViewType == TYPE_ITEM){
            val postViewHolder = holder as PostViewHolder
            postViewHolder.bind(post)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = currentList[position]
        return if (item.id == 0) TYPE_LOADING_ITEM else TYPE_ITEM
    }

    fun setListener(listener: IPost){
        this.listener = listener
    }

    interface IPost{
        fun clickPost(post: Post)
    }
}
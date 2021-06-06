package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
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
    var recyclerViewState: Parcelable? = null //save recyclerView state

    private val posts: ArrayList<Post> = arrayListOf()

    fun getPosts(): ArrayList<Post> {
        return posts
    }

    private fun loadPosts() = viewModelScope.launch {
        postRepository.getPosts(1, 10).let {
            if (it.status == Resource.Status.SUCCESS){
                it.data?.let { list ->
                    val refreshEvery = 10
                    var counter = 0

                    for (post in list){
                        //getting user data
                        val user = getUser(post.userId)
                        post.user = user
                        posts.add(post)
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
            } else if (it.status == Resource.Status.ERROR && Resource.isNoInternetConnection(it.message)) {
                context.toast("No internet connection")

                //load from cache
                postRepository.getPostsFromDb()?.let { list ->
                    posts.clear()
                    posts.addAll(list)
                }

                isRefreshList.value = true
            }
        }
    }

    private suspend fun getUser(userId: Int): User? {
        return userRepository.getUser(userId).data
    }

    init {
        loadPosts()
    }
}

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(Companion) {

    private var listener: IPost? = null

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    companion object: DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem === newItem
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(layoutInflater)

        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = getItem(position)
        holder.binding.post = currentPost
        holder.binding.executePendingBindings()

        holder.itemView.setOnClickListener {
            listener?.clickPost(currentPost)
        }
    }

    fun setListener(listener: IPost){
        this.listener = listener
    }

    interface IPost{
        fun clickPost(post: Post)
    }
}
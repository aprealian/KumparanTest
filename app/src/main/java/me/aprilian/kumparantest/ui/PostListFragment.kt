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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.databinding.FragmentPostListBinding
import me.aprilian.kumparantest.databinding.ItemPostBinding
import me.aprilian.kumparantest.repository.PostRepository
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
        binding.adapter = adapter
        postListViewModel.getPosts().let { adapter.submitList(it) }

        adapter.setListener(object: PostAdapter.IPost{
            override fun clickPost(post: Post) {
                requireContext().toast(post.title)
            }
        })
    }
}

@HiltViewModel
class PostListViewModel @Inject constructor(
   @ApplicationContext private val mContext: Context,
   private val postRepository: PostRepository
): ViewModel(){
    val isRefreshList: MutableLiveData<Boolean> = MutableLiveData()
    private val posts: ArrayList<Post> = arrayListOf()

    fun getPosts(): ArrayList<Post> {
        return posts
    }

    private fun loadPosts() = viewModelScope.launch {
        //posts.addAll(Post.getSample())
        postRepository.getPosts(1, 10).let {
            if (it.isSuccessful){
                it.body()?.let { list ->
                    posts.addAll(list)
                    isRefreshList.value = true
                }
            }

            mContext.toast("isSuccess: "+it.isSuccessful + " " + it.body()?.size)
        }
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
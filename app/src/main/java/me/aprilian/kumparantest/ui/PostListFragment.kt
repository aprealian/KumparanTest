package me.aprilian.kumparantest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.databinding.FragmentPostListBinding
import me.aprilian.kumparantest.databinding.ItemPostBinding
import javax.inject.Inject

class PostListFragment : Fragment() {

    private val homeViewModel: PostListViewModel by viewModels()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter(){
        adapter = PostAdapter()
        binding.adapter = adapter
        homeViewModel.getPosts().let { adapter.submitList(it) }
    }
}

@HiltViewModel
class PostListViewModel @Inject constructor(): ViewModel(){
    private val posts: ArrayList<Post> = arrayListOf()

    fun getPosts(): ArrayList<Post> {
        return posts
    }

    private fun loadCoins() = viewModelScope.launch {
        posts.addAll(Post.getSample())
    }

    init {
        loadCoins()
    }
}

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(Companion) {

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
    }
}
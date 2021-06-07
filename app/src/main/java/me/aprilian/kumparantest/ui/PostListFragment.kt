package me.aprilian.kumparantest.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.R
import me.aprilian.kumparantest.data.*
import me.aprilian.kumparantest.databinding.FragmentPostListBinding
import me.aprilian.kumparantest.databinding.ItemPostBinding
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.repository.UserRepository
import me.aprilian.kumparantest.ui.base.BaseRVAdapter
import me.aprilian.kumparantest.utils.SpacesItemDecoration
import me.aprilian.kumparantest.utils.Utils.dpToPx
import javax.inject.Inject

@AndroidEntryPoint
class PostListFragment : Fragment() {
    private val postListViewModel: PostListViewModel by viewModels()
    private lateinit var binding: FragmentPostListBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner
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

@HiltViewModel
class PostListViewModel @Inject constructor(
   @ApplicationContext private val context: Context,
   private val postRepository: PostRepository,
   private val userRepository: UserRepository
): ViewModel(){

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    private fun getPosts(){
        viewModelScope.launch {
            val result = postRepository.getPosts()
            if (result.status == Resource.Status.SUCCESS && !result.data.isNullOrEmpty()){
                val temps = ArrayList<Post>()
                val refreshEvery = 10

                for (post in result.data){
                    //getting user data
                    val user = getUser(post.userId)
                    post.user = user
                    temps.add(post)

                    //check condition to refresh list
                    //Note: update partially to adapter to reduce waiting/loading time
                    if (temps.size % refreshEvery == 0){
                        _posts.value = Resource(Resource.Status.SUCCESS, temps, "")
                    }
                }

                _posts.value = Resource(Resource.Status.SUCCESS, temps, "")
            } else {
                _posts.value = result
            }
        }
    }

    private suspend fun getUser(userId: Int): User? {
        return userRepository.getUser(userId).data
    }

    init {
        getPosts()
    }
}

class PostAdapter(ctx: Context?, resource: Resource<List<Post>>, private val clickListener: (Post?) -> Unit) : BaseRVAdapter<Post>(ctx, resource, loadingLayout = R.layout.item_post_loading) {

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun createDataViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return PostViewHolder(ItemPostBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            val post = resource.data?.get(position)
            holder.binding.post = post
            holder.binding.executePendingBindings()
            holder.itemView.setOnClickListener {
                clickListener(post)
            }
        }
    }
}
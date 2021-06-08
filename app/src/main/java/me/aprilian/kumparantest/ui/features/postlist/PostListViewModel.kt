package me.aprilian.kumparantest.ui.features.postlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
        private val postRepository: PostRepository,
        private val userRepository: UserRepository
): ViewModel(){

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    fun loadPosts(){
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
        loadPosts()
    }
}
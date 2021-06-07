package me.aprilian.kumparantest.ui.features.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.repository.PostRepository
import me.aprilian.kumparantest.repository.UserRepository
import javax.inject.Inject

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
package me.aprilian.kumparantest.ui.features.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.aprilian.kumparantest.data.Album
import me.aprilian.kumparantest.data.Photo
import me.aprilian.kumparantest.data.Resource
import me.aprilian.kumparantest.data.User
import me.aprilian.kumparantest.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
        private val userRepository: UserRepository
): ViewModel(){
    var userId: Int? = null

    private val _user = MutableLiveData<Resource<User>>()
    val user: LiveData<Resource<User>> = _user

    fun getUser(){
        viewModelScope.launch {
            userId?.let {
                _user.value = userRepository.getUser(it)
                getUser()
            }
        }
    }

    private val _albums = MutableLiveData<Resource<List<Album>>>()
    val albums: LiveData<Resource<List<Album>>> = _albums

    fun getAlbums(){
        viewModelScope.launch {
            userId?.let {
                val albumsResult = userRepository.getUserAlbums(it)
                albumsResult.data?.let { list ->
                    for (album in list){
                        getAlbumPhotos(album.id)?.let { it ->
                            album.photos = it
                        }
                    }
                    _albums.value = Resource(Resource.Status.SUCCESS, list, "")
                }
            } ?: run { _albums.value = Resource(Resource.Status.EMPTY, arrayListOf(), "") }
        }
    }

    private suspend fun getAlbumPhotos(albumId: Int): ArrayList<Photo>? {
        return userRepository.getPhotos(albumId).data
    }
}
package me.aprilian.kumparantest.repository

import me.aprilian.kumparantest.api.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUser(id:Int) = apiService.getUser(id)

    suspend fun getUserAlbums(userId:Int) = apiService.getUserAlbums(userId)

    suspend fun getPhotos(albumId:Int) = apiService.getPhotos(albumId)
}
package me.aprilian.kumparantest.repository

import me.aprilian.kumparantest.datasource.api.ApiService
import me.aprilian.kumparantest.datasource.api.BaseDataSource
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService
): BaseDataSource() {
    suspend fun getUser(id:Int) = getResult { apiService.getUser(id) }

    suspend fun getUserAlbums(userId:Int) = getResult { apiService.getUserAlbums(userId) }

    suspend fun getPhotos(albumId:Int) = getResult { apiService.getPhotos(albumId) }
}
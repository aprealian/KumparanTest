package me.aprilian.kumparantest.repository

import me.aprilian.kumparantest.api.ApiService
import me.aprilian.kumparantest.api.BaseDataSource
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val apiService: ApiService
): BaseDataSource() {
    suspend fun getPosts(start:Int = 1, limit: Int = 10) = getResult { apiService.getPosts(start, limit) }

    suspend fun getPost(id:Int = 1) = getResult { apiService.getPost(id) }

    suspend fun getPostComments(id:Int = 1) = getResult { apiService.getPostComments(id) }
}
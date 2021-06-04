package me.aprilian.kumparantest.repository

import me.aprilian.kumparantest.api.ApiService
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPosts(start:Int = 1, limit: Int = 10) = apiService.getPosts(start, limit)
}
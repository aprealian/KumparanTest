package me.aprilian.kumparantest.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import me.aprilian.kumparantest.datasource.api.ApiService
import me.aprilian.kumparantest.datasource.api.BaseDataSource
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.datasource.local.LocalDataStorage
import javax.inject.Inject

class PostRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
): BaseDataSource() {
    suspend fun getPosts(start:Int = 1, limit: Int = 10) = getResult { apiService.getPosts(start, limit) }

    suspend fun getPost(id:Int = 1) = getResult { apiService.getPost(id) }

    suspend fun getPostComments(id:Int = 1) = getResult { apiService.getPostComments(id) }

    fun savePosts(list: ArrayList<Post>){
        LocalDataStorage.getInstance(context)?.addPosts(list)
    }

    fun getPostsFromDb(): List<Post>? {
        return LocalDataStorage.getInstance(context)?.getPosts
    }
}
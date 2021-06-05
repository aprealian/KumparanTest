package me.aprilian.kumparantest.api

import me.aprilian.kumparantest.data.Comment
import me.aprilian.kumparantest.data.Post
import me.aprilian.kumparantest.data.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<List<Post>>

    @GET("/posts/{id}")
    suspend fun getPost(
        @Path("id") id: Int = 1
    ): Response<Post>

    @GET("/posts/{postId}/comments")
    suspend fun getPostComments(
        @Path("postId") postId: Int = 1
    ): Response<List<Comment>>

    @GET("/users/{id}")
    suspend fun getUser(
        @Path("id") id: Int = 1
    ): Response<User>
}
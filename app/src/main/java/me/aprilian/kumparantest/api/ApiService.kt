package me.aprilian.kumparantest.api

import me.aprilian.kumparantest.data.*
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

    @GET("/users/{userId}/albums")
    suspend fun getUserAlbums(
        @Path("userId") userId: Int = 1
    ): Response<AlbumsResponse>

    @GET("/albums/1/photos")
    suspend fun getPhotos(
        @Path("albumId") albumId: Int = 1
    ): Response<PhotoResponse>
}
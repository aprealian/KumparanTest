package me.aprilian.kumparantest.datasource.api

import me.aprilian.kumparantest.data.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("/posts/{id}")
    suspend fun getPost(
        @Path("id") id: Int
    ): Response<Post>

    @GET("/posts/{postId}/comments")
    suspend fun getPostComments(
        @Path("postId") postId: Int
    ): Response<List<Comment>>

    @GET("/users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): Response<User>

    @GET("/users/{userId}/albums")
    suspend fun getUserAlbums(
        @Path("userId") userId: Int
    ): Response<ArrayList<Album>>

    @GET("/albums/{albumId}/photos")
    suspend fun getPhotos(
        @Path("albumId") albumId: Int
    ): Response<ArrayList<Photo>>
}
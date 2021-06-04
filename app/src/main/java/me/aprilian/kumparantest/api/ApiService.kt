package me.aprilian.kumparantest.api

import me.aprilian.kumparantest.data.PostResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<PostResponse>
}
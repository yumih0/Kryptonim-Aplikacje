package com.example.projektaplikacje.model

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("posts/{postId}")
    suspend fun getPostByID(@Path("postId")postId: Int): Post

    @GET("users")
    suspend fun getAllUsers(): List<User>

    @GET("users/{userId}")
    suspend fun getUserByID(@Path("userId")userId: Int): User

    @GET("todos")
    suspend fun getTodos(@Query("userId")userId: Int): List<Todo>
}
package com.example.projektaplikacje.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.get


class PostRepository(private val api: ApiService) {

    suspend fun getPosts(): List<Post> {
        return api.getPosts()
    }

    suspend fun getPostById(postId: Int): Post {
        return api.getPostByID(postId)
    }

    suspend fun getPostsAndUsers(): List<PostAndUser> = withContext(Dispatchers.IO) {
        val posts = getPosts()
        val users = api.getAllUsers()
        val userMap = users.associateBy { it.id }
        posts.mapNotNull { post ->
            userMap[post.userId]?.let { user ->
                PostAndUser(post, user)
            }
        }
    }
}


class UserRepository(private val api: ApiService) {

    suspend fun getAllUsers(): List<User> {
        return api.getAllUsers()
    }

    suspend fun getUserById(userId: Int): User {
        return api.getUserByID(userId)
    }

    suspend fun getTodosByUserId(userId: Int): List<Todo> {
        return api.getTodos(userId)
    }
}
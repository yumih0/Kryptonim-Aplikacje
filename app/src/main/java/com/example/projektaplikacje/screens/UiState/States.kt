package com.example.projektaplikacje.screens.UiState

import com.example.projektaplikacje.model.Post
import com.example.projektaplikacje.model.PostAndUser
import com.example.projektaplikacje.model.Todo
import com.example.projektaplikacje.model.User

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val posts: List<PostAndUser>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

sealed class PostDetailUiState {
    object Loading : PostDetailUiState()
    data class Success(val post: Post) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}

sealed class UserDetailUiState {
    object Loading : UserDetailUiState()
    data class Success(val user: User, val todos: List<Todo>) : UserDetailUiState()
    data class Error(val message: String) : UserDetailUiState()
}


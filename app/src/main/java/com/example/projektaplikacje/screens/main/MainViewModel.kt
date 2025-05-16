package com.example.projektaplikacje.screens.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektaplikacje.model.PostAndUser
import com.example.projektaplikacje.model.PostRepository
import com.example.projektaplikacje.model.UserRepository
import com.example.projektaplikacje.screens.UiState.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {LoadData()}

    fun LoadData() {
        viewModelScope.launch {
            try {
                val posts = postRepository.getPosts()
                val users = userRepository.getAllUsers()
                val userMap = users.associateBy { it.id }

                val postWithUsers = posts.mapNotNull { post ->
                    userMap[post.userId]?.let { user ->
                        PostAndUser(post, user)
                    }
                }

                _uiState.value = MainUiState.Success(postWithUsers)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Napotkano błąd podczas ładowania.")
            }
        }
    }
}
package com.example.projektaplikacje.screens.postDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektaplikacje.model.PostRepository
import com.example.projektaplikacje.screens.UiState.PostDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostDetailViewModel(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: Int) {
        viewModelScope.launch {
            try {
                val post = postRepository.getPostById(postId)
                _uiState.value = PostDetailUiState.Success(post)
            } catch (e: Exception) {
                _uiState.value = PostDetailUiState.Error(e.message ?: "Napotkano błąd podczas ładowania.")
            }
        }
    }
}
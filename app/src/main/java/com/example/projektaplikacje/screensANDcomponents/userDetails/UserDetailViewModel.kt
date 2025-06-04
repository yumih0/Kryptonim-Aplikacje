package com.example.projektaplikacje.screensANDcomponents.userDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektaplikacje.model.UserRepository
import com.example.projektaplikacje.screensANDcomponents.UiState.UserDetailUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailUiState>(UserDetailUiState.Loading)
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    fun loadUserDetails(userId: Int) {
        viewModelScope.launch {
            try {
                val userDeferred = async { userRepository.getUserById(userId) }
                val todosDeferred = async { userRepository.getTodosByUserId(userId) }

                val user = userDeferred.await()
                val todos = todosDeferred.await()

                _uiState.value = UserDetailUiState.Success(user, todos)
            } catch (e: Exception) {
                _uiState.value = UserDetailUiState.Error(e.message ?: "Napotkano błąd podczas ładowania.")
            }
        }
    }
}
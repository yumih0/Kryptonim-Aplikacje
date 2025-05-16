package com.example.projektaplikacje.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projektaplikacje.screens.main.MainViewModel
import com.example.projektaplikacje.screens.postDetails.PostDetailViewModel
import com.example.projektaplikacje.screens.userDetails.UserDetailViewModel


class ViewModelFactory(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(postRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(PostDetailViewModel::class.java) -> {
                PostDetailViewModel(postRepository) as T
            }
            modelClass.isAssignableFrom(UserDetailViewModel::class.java) -> {
                UserDetailViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

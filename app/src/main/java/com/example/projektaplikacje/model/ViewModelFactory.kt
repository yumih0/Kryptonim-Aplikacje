package com.example.projektaplikacje.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projektaplikacje.screensANDcomponents.main.MainViewModel
import com.example.projektaplikacje.screensANDcomponents.postDetails.PostDetailViewModel
import com.example.projektaplikacje.screensANDcomponents.userDetails.UserDetailViewModel


class ViewModelFactory(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(postRepository, userRepository, context) as T
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

package com.example.projektaplikacje.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projektaplikacje.model.PostRepository
import com.example.projektaplikacje.model.RetrofitInstance
import com.example.projektaplikacje.model.UserRepository
import com.example.projektaplikacje.model.ViewModelFactory
import com.example.projektaplikacje.screens.main.MainScreen
import com.example.projektaplikacje.screens.main.MainViewModel
import com.example.projektaplikacje.screens.postDetails.PostDetailScreen
import com.example.projektaplikacje.screens.postDetails.PostDetailViewModel
import com.example.projektaplikacje.screens.userDetails.UserDetailScreen
import com.example.projektaplikacje.screens.userDetails.UserDetailViewModel
import com.example.projektaplikacje.ui.theme.ProjektAplikacjeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitInstance.apiService
        val postRepository = PostRepository(apiService)
        val userRepository = UserRepository(apiService)
        val factory = ViewModelFactory(postRepository, userRepository)

        setContent {
            ProjektAplikacjeTheme(darkTheme = true) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        val viewModel: MainViewModel = viewModel(factory = factory)
                        MainScreen(viewModel = viewModel, navController = navController)
                    }

                    composable("postDetail/{postId}") { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
                        val postDetailViewModel: PostDetailViewModel = viewModel(factory = factory)

                        postId?.let {
                            LaunchedEffect(Unit) {
                                postDetailViewModel.loadPost(it)
                            }
                            PostDetailScreen(viewModel = postDetailViewModel, navController = navController)
                        }
                    }

                    composable("userDetail/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                        val userDetailViewModel: UserDetailViewModel = viewModel(factory = factory)

                        userId?.let {
                            LaunchedEffect(Unit) {
                                userDetailViewModel.loadUserDetails(it)
                            }
                            UserDetailScreen(viewModel = userDetailViewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}



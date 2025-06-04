package com.example.projektaplikacje.screensANDcomponents

import FavoritesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projektaplikacje.model.PostRepository
import com.example.projektaplikacje.model.RetrofitInstance
import com.example.projektaplikacje.model.UserRepository
import com.example.projektaplikacje.model.ViewModelFactory
import com.example.projektaplikacje.screensANDcomponents.Profile.ProfileScreen
import com.example.projektaplikacje.screensANDcomponents.main.MainScreen
import com.example.projektaplikacje.screensANDcomponents.main.MainViewModel
import com.example.projektaplikacje.screensANDcomponents.postDetails.PostDetailScreen
import com.example.projektaplikacje.screensANDcomponents.postDetails.PostDetailViewModel
import com.example.projektaplikacje.screensANDcomponents.userDetails.UserDetailScreen
import com.example.projektaplikacje.screensANDcomponents.userDetails.UserDetailViewModel
import com.example.projektaplikacje.ui.theme.ProjektAplikacjeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitInstance.apiService
        val postRepository = PostRepository(apiService)
        val userRepository = UserRepository(apiService)
        val factory = ViewModelFactory(postRepository, userRepository, applicationContext)

        setContent {
            ProjektAplikacjeTheme(darkTheme = true) {
                val navController = rememberNavController()

                val mainViewModel: MainViewModel = viewModel(factory = factory)

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(viewModel = mainViewModel, navController = navController)
                    }

                    composable("postDetail/{postId}") { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull()
                        val postDetailViewModel: PostDetailViewModel = viewModel(factory = factory)

                        postId?.let {
                            LaunchedEffect(Unit) {
                                postDetailViewModel.loadPost(it)
                            }
                            PostDetailScreen(
                                viewModel = postDetailViewModel,
                                navController = navController
                            )
                        }
                    }

                    composable("userDetail/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                        val userDetailViewModel: UserDetailViewModel = viewModel(factory = factory)

                        userId?.let {
                            LaunchedEffect(Unit) {
                                userDetailViewModel.loadUserDetails(it)
                            }
                            UserDetailScreen(
                                viewModel = userDetailViewModel,
                                navController = navController
                            )
                        }
                    }

                    composable("profile") {
                        ProfileScreen(navController = navController)
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            viewModel = mainViewModel,
                            onPostClick = { navController.navigate("postDetail/$it") },
                            onUserClick = { navController.navigate("userDetail/$it") },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}




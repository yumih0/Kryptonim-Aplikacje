package com.example.projektaplikacje.screensANDcomponents.main

import android.content.Context
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektaplikacje.datastore.UserPreferences
import com.example.projektaplikacje.model.PostAndUser
import com.example.projektaplikacje.model.PostRepository
import com.example.projektaplikacje.model.UserRepository
import com.example.projektaplikacje.screensANDcomponents.UiState.MainUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var allPosts: List<PostAndUser> = emptyList()

    //searching posts
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    //fav posts
    private val favoritePostIds = mutableSetOf<Int>()
    private val maxFavorites = 10
    private val _favoriteLimitReached = MutableStateFlow(false)
    val favoriteLimitReached: StateFlow<Boolean> = _favoriteLimitReached.asStateFlow()

    private val _showFavoriteLimitSnackbar = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val showFavoriteLimitSnackbar = _showFavoriteLimitSnackbar.asSharedFlow()


    init {
        viewModelScope.launch {
            UserPreferences.getFavorites(context).collect {
                favoritePostIds.clear()
                favoritePostIds.addAll(it)
                applySearch()
            }
        }
        LoadData()
    }

    fun LoadData() {
        viewModelScope.launch {
            try {
                val posts = postRepository.getPosts()
                val users = userRepository.getAllUsers()
                val userMap = users.associateBy { it.id }

                allPosts = posts.mapNotNull { post ->
                    userMap[post.userId]?.let { user -> PostAndUser(post, user) }
                }

                applySearch()
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Napotkano błąd podczas ładowania.")
            }
        }
    }

    //searching posts
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applySearch()
    }

    //fav posts
    fun toggleFavorite(postId: Int) {
        if (favoritePostIds.contains(postId)) {
            favoritePostIds.remove(postId)
        } else {
            if (favoritePostIds.size >= maxFavorites) {
                _showFavoriteLimitSnackbar.tryEmit(Unit)
                return
            }
            favoritePostIds.add(postId)
        }

        viewModelScope.launch {
            UserPreferences.saveFavorites(context, favoritePostIds)
        }

        applySearch()
    }
    
    private fun applySearch() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isBlank()) {
            allPosts
        } else {
            allPosts.filter {
                it.user.name.contains(query, ignoreCase = true) ||
                        it.post.title.contains(query, ignoreCase = true)
            }
        }
        val withFavorites = filtered.map {
            it.copy(isFavorite = favoritePostIds.contains(it.post.id))
        }

        _uiState.value = MainUiState.Success(withFavorites)
    }

}
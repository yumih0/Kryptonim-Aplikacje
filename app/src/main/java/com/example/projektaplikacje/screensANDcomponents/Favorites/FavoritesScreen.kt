import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.projektaplikacje.screensANDcomponents.UiState.MainUiState
import com.example.projektaplikacje.screensANDcomponents.main.BottomNavigationBar
import com.example.projektaplikacje.screensANDcomponents.main.MainViewModel
import com.example.projektaplikacje.screensANDcomponents.main.PostItem

@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onPostClick: (Int) -> Unit,
    onUserClick: (Int) -> Unit,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    val favoritePosts = when (state) {
        is MainUiState.Success -> {
            (state as MainUiState.Success).posts.filter { it.isFavorite }
        }
        else -> emptyList()
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar { route ->
                navController.navigate(route)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            items(favoritePosts) { postAndUser ->
                PostItem(
                    PostAndUser = postAndUser,
                    onPostClick = { onPostClick(postAndUser.post.id) },
                    onUserClick = { onUserClick(postAndUser.user.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(it) }
                )
            }
        }
    }
}


package com.example.projektaplikacje.screensANDcomponents.main

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projektaplikacje.model.PostAndUser
import com.example.projektaplikacje.screensANDcomponents.UiState.MainUiState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.HistoricalChange
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import retrofit2.http.Query

@Composable
fun MainScreen(viewModel: MainViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
//    val favoriteLimitReached by viewModel.favoriteLimitReached.collectAsState()
//    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var lastSnackbarTime by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        viewModel.showFavoriteLimitSnackbar.collect {
            val now = System.currentTimeMillis()
            if (now - lastSnackbarTime > 2000) {
                lastSnackbarTime = now
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Osiągnięto limit polubionych postów")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = { viewModel.onSearchQueryChanged(it) }
            )
        },
        bottomBar = {
            BottomNavigationBar { route ->
                navController.navigate(route)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        when (state) {
            is MainUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MainUiState.Error -> {
                val message = (state as MainUiState.Error).message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Błąd: $message")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.LoadData() }) {
                        Text("Spróbuj ponownie")
                    }
                }
            }

            is MainUiState.Success -> {
                val posts = (state as MainUiState.Success).posts
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(posts) { postAndUser ->
                        PostItem(
                            PostAndUser = postAndUser,
                            onPostClick = { postId ->
                                navController.navigate("postDetail/$postId")
                            },
                            onUserClick = { userId ->
                                navController.navigate("userDetail/$userId")
                            },
                            onFavoriteClick = {viewModel.toggleFavorite(it)}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    PostAndUser: PostAndUser,
    onPostClick: (Int) -> Unit,
    onUserClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onPostClick(PostAndUser.post.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = PostAndUser.post.title.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { onFavoriteClick(PostAndUser.post.id) }) {
                    Icon(
                        imageVector = if (PostAndUser.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = PostAndUser.post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = PostAndUser.user.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onUserClick(PostAndUser.user.id) }
                )
            }
        }
    }
}


@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "Jaki post wariacie?",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun BottomNavigationBar(
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .clickable { onNavigate("profile") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .clickable { onNavigate("main") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background
            )
        }


        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .clickable { onNavigate("favorites") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}







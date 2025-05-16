package com.example.projektaplikacje.screens.main

import android.graphics.drawable.Icon
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projektaplikacje.model.PostAndUser
import com.example.projektaplikacje.screens.UiState.MainUiState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is MainUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize() .background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MainUiState.Error -> {
            val message = (state as MainUiState.Error).message
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
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
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(posts) { PostAndUser ->
                    PostItem(
                        PostAndUser = PostAndUser,
                        onPostClick = { postId ->
                            navController.navigate("postDetail/$postId")
                        },
                        onUserClick = { userId ->
                            navController.navigate("userDetail/$userId")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PostItem(
    PostAndUser: PostAndUser,
    onPostClick: (Int) -> Unit,
    onUserClick: (Int) -> Unit
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
            Text(
                text = PostAndUser.post.title.replaceFirstChar { it.uppercaseChar() },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = PostAndUser.post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
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



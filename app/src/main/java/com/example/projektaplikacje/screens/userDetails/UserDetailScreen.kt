package com.example.projektaplikacje.screens.userDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projektaplikacje.screens.UiState.UserDetailUiState

import androidx.compose.runtime.getValue

@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is UserDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize() .background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UserDetailUiState.Error -> {
            val message = (state as UserDetailUiState.Error).message
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
                Button(onClick = { navController.popBackStack() }) {
                    Text("Powrót")
                }
            }
        }

        is UserDetailUiState.Success -> {
            val user = (state as UserDetailUiState.Success).user
            val todos = (state as UserDetailUiState.Success).todos

            Column(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)) {

                Text(text = user.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Text(text = "@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Email: ${user.email}", color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "Telefon: ${user.phone}",color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "Strona: ${user.website}", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Firma: ${user.company.name}", color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "Adres: ${user.address.street} ${user.address.suite}, ${user.address.city} (${user.address.zipcode})", color = MaterialTheme.colorScheme.onPrimary)

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Zadania:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimary)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(todos) { todo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = todo.completed,
                                onCheckedChange = null
                            )
                            Text(
                                text = todo.title,
                                modifier = Modifier.padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Powrót",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
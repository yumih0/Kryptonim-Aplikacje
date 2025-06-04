package com.example.projektaplikacje.screensANDcomponents.userDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projektaplikacje.screensANDcomponents.UiState.UserDetailUiState
import com.example.projektaplikacje.screensANDcomponents.userDetails.UserDetailViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is UserDetailUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
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

            val rawLat = user.address.geo.lat
            val rawLng = user.address.geo.lng

            val lat = rawLat.replace(",", ".").toDoubleOrNull() ?: 0.0
            val lng = rawLng.replace(",", ".").toDoubleOrNull() ?: 0.0
            val location = LatLng(lat, lng)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 12f)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(text = user.name, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                Text(text = "@${user.username}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Email: ${user.email}", color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "Telefon: ${user.phone}", color = MaterialTheme.colorScheme.onPrimary)
                Text(text = "Strona: ${user.website}", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Firma: ${user.company.name}", color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = "Adres: ${user.address.street} ${user.address.suite}, ${user.address.city} (${user.address.zipcode})",
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Lokalizacja na mapie:", style = MaterialTheme.typography.titleMedium)

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = location),
                        title = user.name,
                        snippet = user.address.city
                    )
                }

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

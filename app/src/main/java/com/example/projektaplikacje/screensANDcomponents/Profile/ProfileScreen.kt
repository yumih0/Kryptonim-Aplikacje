package com.example.projektaplikacje.screensANDcomponents.Profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projektaplikacje.datastore.UserPreferences
import com.example.projektaplikacje.screensANDcomponents.main.BottomNavigationBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    val getImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(context, it)
            imagePath = savedPath
        }
    }

    LaunchedEffect(Unit) {
        UserPreferences.getUserData(context).collectLatest { (first, last, image) ->
            firstName = first
            lastName = last
            imagePath = image
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar { route -> navController.navigate(route) }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imagePath.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(File(context.filesDir, imagePath)),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .clickable { getImage.launch("image/*") }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { getImage.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Wybierz zdjęcie", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Imię") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Nazwisko") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                coroutineScope.launch {
                    UserPreferences.saveUserData(context, firstName, lastName, imagePath)
                }
            }) {
                Text("Zapisz")
            }
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    val fileName = UUID.randomUUID().toString() + ".jpg"
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val outputStream: OutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)

    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return fileName
}
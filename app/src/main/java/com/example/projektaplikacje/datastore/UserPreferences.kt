package com.example.projektaplikacje.datastore


import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {

    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
    private val KEY_LAST_NAME = stringPreferencesKey("last_name")
    private val KEY_IMAGE_PATH = stringPreferencesKey("image_path")
    private val KEY_FAVORITES = stringSetPreferencesKey("favorite_posts")

    // saving user details
    suspend fun saveUserData(context: Context, firstName: String, lastName: String, imagePath: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FIRST_NAME] = firstName
            prefs[KEY_LAST_NAME] = lastName
            prefs[KEY_IMAGE_PATH] = imagePath
        }
    }

    // reading user details
    fun getUserData(context: Context): Flow<Triple<String, String, String>> {
        return context.dataStore.data.map { prefs ->
            Triple(
                prefs[KEY_FIRST_NAME] ?: "",
                prefs[KEY_LAST_NAME] ?: "",
                prefs[KEY_IMAGE_PATH] ?: ""
            )
        }
    }

    // saving fav posts
    suspend fun saveFavorites(context: Context, favorites: Set<Int>) {
        context.dataStore.edit { prefs ->
            prefs[KEY_FAVORITES] = favorites.map { it.toString() }.toSet()
        }
    }

    // reading fav posts
    fun getFavorites(context: Context): Flow<Set<Int>> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_FAVORITES]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
        }
    }
}
package com.example.projektaplikacje.screensANDcomponents.Profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projektaplikacje.datastore.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val context: Context) : ViewModel() {
    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _imagePath = MutableStateFlow("")
    val imagePath = _imagePath.asStateFlow()

    init {
        viewModelScope.launch {
            UserPreferences.getUserData(context).collect {
                _firstName.value = it.first
                _lastName.value = it.second
                _imagePath.value = it.third
            }
        }
    }

    fun updateData(first: String, last: String, imagePath: String) {
        viewModelScope.launch {
            UserPreferences.saveUserData(context, first, last, imagePath)
        }
    }

    fun setImagePath(path: String) {
        _imagePath.value = path
    }
}

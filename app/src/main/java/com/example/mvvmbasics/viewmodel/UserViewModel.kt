package com.example.mvvmbasics.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mvvmbasics.data.model.User
import com.example.mvvmbasics.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class for UI states
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    private val _filteredUsers = MutableStateFlow<List<User>>(emptyList())
    val filteredUsers: StateFlow<List<User>> = _filteredUsers.asStateFlow()
    
    // For backward compatibility
    val users: StateFlow<List<User>> = _filteredUsers.asStateFlow()

    init {
        loadUsers()
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.getUsers().collect { fetchedUsers ->
                    _allUsers.value = fetchedUsers
                    if (fetchedUsers.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(fetchedUsers)
                        filterUsers()
            }
        }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    
    fun searchUsers(query: String) {
        _searchQuery.value = query
        filterUsers()
    }
    
    private fun filterUsers() {
        val query = _searchQuery.value.lowercase().trim()
        val users = _allUsers.value
        
        _filteredUsers.value = if (query.isEmpty()) {
            users
        } else {
            users.filter { user ->
                user.name.lowercase().contains(query) ||
                user.username.lowercase().contains(query) ||
                user.email.lowercase().contains(query) ||
                user.company.name.lowercase().contains(query)
            }
        }
    }
    
    fun refreshUsers() {
        loadUsers()
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        filterUsers()
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

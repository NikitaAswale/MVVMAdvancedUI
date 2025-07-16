package com.example.mvvmbasics.data.repository

import com.example.mvvmbasics.data.model.User
import com.example.mvvmbasics.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Repository to fetch users from ApiService and expose as Flow
class UserRepository(private val apiService: ApiService) {
    fun getUsers(): Flow<List<User>> = flow {
        emit(apiService.getUsers())
    }
}

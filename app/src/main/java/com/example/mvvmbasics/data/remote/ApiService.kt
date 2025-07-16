package com.example.mvvmbasics.data.remote

import retrofit2.http.GET
import com.example.mvvmbasics.data.model.User

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>
}

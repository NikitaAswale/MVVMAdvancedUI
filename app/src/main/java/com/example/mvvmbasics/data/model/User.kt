package com.example.mvvmbasics.data.model

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String = "",
    val website: String = "",
    val address: Address = Address(),
    val company: Company = Company()
)

data class Address(
    val street: String = "",
    val suite: String = "",
    val city: String = "",
    val zipcode: String = "",
    val geo: Geo = Geo()
)

data class Geo(
    val lat: String = "",
    val lng: String = ""
)

data class Company(
    val name: String = "",
    val catchPhrase: String = "",
    val bs: String = ""
)

package com.plb.conference.domain.models

data class User(
    val id: Long,
    val full_name: String,
    val company: String,
    val email: String,
    val password: String
)
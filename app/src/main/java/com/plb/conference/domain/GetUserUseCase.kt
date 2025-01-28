package com.plb.conference.domain

import com.plb.conference.domain.models.User

class GetUserUseCase {
    fun getUser(): User {
        return User(
            id = 1,
            full_name = "John Doe",
            company = "Google",
            email = "test@test.com",
            password = "123",
        )
    }
}
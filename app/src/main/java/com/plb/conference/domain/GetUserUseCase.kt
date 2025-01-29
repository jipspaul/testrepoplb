package com.plb.conference.domain

import com.plb.conference.domain.models.User
import com.plb.conference.repositories.NetworkModule

class GetUserUseCase {
    suspend fun getUser(mail:String): User? {

        // Get all db user
        val users = NetworkModule.userApi.getUsers()

        // Get list of users
        val listUser = users.result

        // Find user by email
        val myUser = listUser.find { it ->
            it.email == mail
        }

        return myUser
    }
}
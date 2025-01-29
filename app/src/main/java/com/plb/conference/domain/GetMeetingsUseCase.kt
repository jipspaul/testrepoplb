package com.plb.conference.domain

import android.text.format.DateUtils
import com.plb.conference.repositories.Meeting
import com.plb.conference.repositories.NetworkModule
import com.plb.conference.repositories.MeetingResponse

class GetMeetingsUseCase {

    suspend fun getMeetings(id:String): List<Meeting> {

        // Get all db user
        val users = NetworkModule.userApi.getUsers()

        // Get list of users
        val listUser = users.result


        val meetings =  NetworkModule.userApi.getMeetings().result.filter { it ->
            it.user1 == id
        }

       return  meetings.map { meeting ->
            Meeting(
                id = meeting.id,
                user1 = listUser.find { "${it.id}" == meeting.user1 }!!.full_name,
                user2 = listUser.find { "${it.id}" == meeting.user2 }!!.full_name,
                room = meeting.room,
                date = meeting.date,
            )
        }
    }
}
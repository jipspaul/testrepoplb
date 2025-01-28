package com.plb.conference.domain

class MailVerificationUseCase {

    fun isMailOK(mail:String) : Boolean {
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            return false
        } else {
            return true
        }
    }
}
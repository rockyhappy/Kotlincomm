package com.devrachit.kotlincomm.repository

import com.devrachit.kotlincomm.firebaseClient.FirebaseClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val firebaseClient: FirebaseClient
) {
    fun login(userName :String, password :String, isDone:(Boolean,String?) -> Unit)
    {
        firebaseClient.login(userName,password,isDone)
    }

}
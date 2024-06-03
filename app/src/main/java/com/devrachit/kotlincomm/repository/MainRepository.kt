package com.devrachit.kotlincomm.repository

import android.util.Log
import com.devrachit.kotlincomm.firebaseClient.FirebaseClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val firebaseClient: FirebaseClient
) {
    fun login(userName :String, password :String, isDone:(Boolean,String?) -> Unit)
    {
        Log.e("MainRepository",userName)
        firebaseClient.login(userName,password,isDone)
    }

}
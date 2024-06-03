package com.devrachit.kotlincomm.firebaseClient

import android.util.Log
import com.devrachit.kotlincomm.utils.DataModel
import com.devrachit.kotlincomm.utils.FirebaseFieldNames.LATEST_EVENT
import com.devrachit.kotlincomm.utils.FirebaseFieldNames.PASSWORD
import com.devrachit.kotlincomm.utils.FirebaseFieldNames.STATUS
import com.devrachit.kotlincomm.utils.MyEventListener
import com.devrachit.kotlincomm.utils.UserStatus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(

    private val gson: Gson,
    private val dbRef: DatabaseReference

) {
    private var currentUsername:String?=null
    private fun setUsername(username: String){
        this.currentUsername = username
    }
    fun login(username: String, password: String, done: (Boolean, String?) -> Unit) {
        println("FirebaseClient")
        dbRef.child(username)
        dbRef.addListenerForSingleValueEvent(object  : MyEventListener(){
            override fun onDataChange(snapshot: DataSnapshot) {
                println("FirebaseClient2")
                //if the current user exists
                if (snapshot.hasChild(username)){
                    //user exists , its time to check the password
                    val dbPassword = snapshot.child(username).child(PASSWORD).value
                    if (password == dbPassword) {
                        //password is correct and sign in
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                Log.e("FirebaseClient", "User is online")
                                setUsername(username)
                                done(true,null)
                            }.addOnFailureListener {
                                Log.e("FirebaseClient", "User is offline")
                                done(false,"${it.message}")
                            }
                    }else{
                        //password is wrong, notify user
                        done(false,"Password is wrong")
                    }

                }else{
                    //user doesnt exist, register the user
                    println("FirebaseClient3")
                    dbRef.child(username).child(PASSWORD).setValue(password).addOnCompleteListener {
                        dbRef.child(username).child(STATUS).setValue(UserStatus.ONLINE)
                            .addOnCompleteListener {
                                setUsername(username)
                                Log.e("FirebaseClient", "User  online")
                                done(true,null)
                            }.addOnFailureListener {
                                done(false,it.message)
                                Log.e("FirebaseClient", "User  offline")
                            }
                    }.addOnFailureListener {
                        done(false,it.message)
                    }

                }
            }
        })
    }
    fun observeUsersStatus(status: (List<Pair<String, String>>) -> Unit) {
        dbRef.addValueEventListener(object : MyEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.filter { it.key !=currentUsername }.map {
                    it.key!! to it.child(STATUS).value.toString()
                }
                status(list)
            }
        })
    }
    fun subscribeForLatestEvent(listener:Listener){
        try {
            dbRef.child(currentUsername!!).child(LATEST_EVENT).addValueEventListener(
                object : MyEventListener() {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        super.onDataChange(snapshot)
                        val event = try {
                            gson.fromJson(snapshot.value.toString(),DataModel::class.java)
                        }catch (e:Exception){
                            e.printStackTrace()
                            null
                        }
                        event?.let {
                            listener.onLatestEventReceived(it)
                        }
                    }
                }
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun sendMessageToOtherClient(message: DataModel, success:(Boolean) -> Unit){
        val convertedMessage = gson.toJson(message.copy(sender = currentUsername))
        dbRef.child(message.target).child(LATEST_EVENT).setValue(convertedMessage)
            .addOnCompleteListener {
                success(true)
            }.addOnFailureListener {
                success(false)
            }
    }

    fun changeMyStatus(status: UserStatus) {
        dbRef.child(currentUsername!!).child(STATUS).setValue(status.name)
    }

    fun clearLatestEvent() {
        dbRef.child(currentUsername!!).child(LATEST_EVENT).setValue(null)
    }

    fun logOff(function:()->Unit) {
        dbRef.child(currentUsername!!).child(STATUS).setValue(UserStatus.OFFLINE)
            .addOnCompleteListener { function() }
    }
    interface Listener {
        fun onLatestEventReceived(event:DataModel)
    }
}
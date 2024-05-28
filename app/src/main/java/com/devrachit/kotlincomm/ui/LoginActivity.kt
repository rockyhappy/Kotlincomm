package com.devrachit.kotlincomm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devrachit.kotlincomm.R
import com.devrachit.kotlincomm.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var views: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }
    private fun init(){
        views.apply{
            btn.setOnClickListener {

            }
        }
    }
}
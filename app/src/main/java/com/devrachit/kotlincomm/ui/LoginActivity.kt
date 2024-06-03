package com.devrachit.kotlincomm.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devrachit.kotlincomm.R
import com.devrachit.kotlincomm.databinding.ActivityLoginBinding
import com.devrachit.kotlincomm.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var views: ActivityLoginBinding

    @Inject
    lateinit var mainRepository: MainRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init() {
        views.apply {
            btn.setOnClickListener {
                Toast.makeText(this@LoginActivity, "Login Clicked", Toast.LENGTH_SHORT).show()
                mainRepository.login(
                    usernameEt.text.toString(), passwordEt.text.toString()
                ) { isDone, reason ->
                    if (!isDone) {
                        Toast.makeText(this@LoginActivity, reason, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

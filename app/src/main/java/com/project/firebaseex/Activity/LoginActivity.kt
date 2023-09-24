package com.project.firebaseex.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.firebaseex.databinding.ActivityLoginBinding
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var firebaseUser: FirebaseUser ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser

        binding.LoginBtnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.LoginBtnLogin.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val email = binding.LoginEmailEt.text.toString()
        val password = binding.LoginPassEt.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, UploadActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        Objects.requireNonNull(task.exception)!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            .addOnFailureListener({ e -> Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show() })
    }

    override fun onStart() {
        super.onStart()
        if (firebaseUser != null) {
            startActivity(Intent(this, UploadActivity::class.java))
            finish()
        }
    }
}


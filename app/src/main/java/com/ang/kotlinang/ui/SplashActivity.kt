package com.ang.kotlinang.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if (fAuth.currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }
    }
}
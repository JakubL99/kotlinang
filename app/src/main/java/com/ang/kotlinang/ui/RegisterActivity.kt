package com.ang.kotlinang.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ang.kotlinang.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth
    lateinit var fStore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register"
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        tv_mvlogin.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    LoginActivity::class.java
                )
            )
        }
        if (fAuth.currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }


        btn_register.setOnClickListener {
            registerUser()
        }
        showHide()

    }

    private fun registerUser() {
        val email: String = et_email.text.toString().trim()
        val password: String = et_password.text.toString().trim()
        val phone: String = et_phone.text.toString().trim()
        val name: String = et_fullName.text.toString().trim()

        if (name.length < 6) {
            et_fullName.error = "Name must have at least 6 characters"
            return
        } else if (password.length < 8) {
            et_password.error = "Password must have at least 8 characters"
            return
        } else if (email.isEmpty()) {
            et_email.error = "Enter email"
            return
        } else if (phone.isEmpty()) {
            et_phone.error = "Enter phone number"
            return
        }

        pb_register.visibility = View.VISIBLE

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val userID = fAuth.currentUser!!.uid
                val map = HashMap<String, Any>()
                map["Name"] = name
                map["Email"] = email
                map["Phone"] = phone
                map["ProfileImg"] = ""
                fStore.collection("users").document(userID).set(map).addOnSuccessListener {
                    Log.d(
                        "TAG",
                        "onSuccess: user Profile is created for $userID"
                    )
                }.addOnFailureListener { e -> Log.d("TAG", "onFailure: $e") }
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "Registration failed: " + it.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
                pb_register.visibility = View.GONE
            }
        }
    }

    fun showHide() {
        password_toggle.setOnClickListener {
            if (it.tooltipText!!.equals("Show")) {
                it.tooltipText = "Hide"
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                it.tooltipText = "Show"
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}
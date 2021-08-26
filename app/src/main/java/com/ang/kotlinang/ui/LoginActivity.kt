package com.ang.kotlinang.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ang.kotlinang.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        fAuth = FirebaseAuth.getInstance()
        tv_mvregister.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    RegisterActivity::class.java
                )
            )
        }

        btn_login.setOnClickListener { signUser() }
        showHide()
        resetPassword()
    }

    fun signUser() {
        val email: String = et_email.text.toString().trim()
        val password: String = et_password.text.toString().trim()
        pb_login.visibility = View.VISIBLE

        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Login failed: " + it.exception?.message, Toast.LENGTH_SHORT)
                    .show()
                pb_login.visibility = View.GONE

            }
        }
    }

    fun showHide() {
        show_log.setOnClickListener {
            if (it.tooltipText!!.equals("Show")) {
                it.tooltipText = "Hide"
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                it.tooltipText = "Show"
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    fun resetPassword() {
        forgotpass.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.forgot_password, null)
            val username = view.findViewById<EditText>(R.id.namepass)
            builder.setView(view)
            builder.setPositiveButton(
                "Reset",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    forgotPassword(username)
                })

            builder.setNegativeButton(
                "Close",
                DialogInterface.OnClickListener { dialogInterface, i ->
                })
            builder.show()
        }
    }

    fun forgotPassword(username: EditText) {
        if (username.text.toString().isEmpty()) {
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(
                username
                    .text.toString()
            ).matches()
        ) {
            return
        }

        fAuth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
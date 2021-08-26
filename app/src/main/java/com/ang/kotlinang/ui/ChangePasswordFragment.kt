package com.ang.kotlinang.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ang.kotlinang.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_change_password.*


class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userId: String = fAuth.currentUser!!.uid
        backToProfile()
        savePassword()
    }

    fun savePassword() {
        savepassword.setOnClickListener {
            val pass = currpass.text!!.trim().toString()
            if (pass.length > 7) {
                val credential = EmailAuthProvider.getCredential(
                    fAuth.currentUser!!.email.toString(), pass
                )
                fAuth.currentUser!!.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            tlay1.error = ""
                            if (newpass.text!!.trim().length > 7 && confirmpass.text!!.trim().length > 7) {
                                tlay3.error = ""
                                if (newpass.text?.trim().toString()
                                        .equals(confirmpass.text?.trim().toString())
                                ) {
                                    tlay3.error = ""
                                    tlay2.error = ""
                                    fAuth.currentUser!!.updatePassword(
                                        newpass.text?.trim().toString()
                                    )
                                    Toast.makeText(
                                        activity,
                                        "Password Changed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    tlay3.error = "Enter correct password"
                                }
                            } else {
                                if (newpass.text!!.trim().length < 8)
                                    tlay2.error = "Password length less than 8"
                                else if (confirmpass.text!!.trim().length < 8) {
                                    tlay2.error = ""
                                    tlay3.error = "Password length less than 8"
                                }
                            }
                        }
                    }.addOnFailureListener {
                        tlay1.error = "Wrong password"
                    }
            } else {
                tlay1.error = "Password length less than 8"
            }
        }
    }

    fun backToProfile() {
        framepassword.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}


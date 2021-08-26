package com.ang.kotlinang.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ang.kotlinang.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.forgot_password.view.*
import kotlinx.android.synthetic.main.update_profile.view.*
import maes.tech.intentanim.CustomIntent
import java.util.*


class UserActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    lateinit var fAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var mImageUri: Uri
    private lateinit var StorageRef: StorageReference
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("uploads")
        back.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }
        LoadImage()
        setProfile()
        setImage()
        updateProfile()
        changePassword()
    }

    fun setProfile() {
        val userId: String = fAuth.currentUser!!.uid
        db.collection("users").document(userId).get().addOnSuccessListener {
            if (it.exists()) {
                prof_name.text = it.getString("Name")
                prof_email.text = it.getString("Email")
                prof_phone.text = it.getString("Phone")
                main_name.text = it.getString("Name")
            }
        }
    }

    fun setImage() {
        image.setOnClickListener {
            openFileChooser()
        }
    }

    fun openFileChooser() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null
        ) {
            mImageUri = data.data!!
            Picasso.get().load(mImageUri).into(image)
            UploadImage()
        }
    }

    fun UploadImage() {
        if (mImageUri == null) return
        val filename = UUID.randomUUID().toString()
        StorageRef = FirebaseStorage.getInstance().getReference("/uploads/$filename")
        val userId: String = fAuth.currentUser!!.uid
        val uploadTask = StorageRef.putFile(mImageUri)
        uploadTask.continueWith {
            if (!it.isSuccessful) {
                it.exception?.let { t ->
                    throw t
                }
            }
            StorageRef.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.addOnSuccessListener { task ->
                    val myUri = task.toString()
                    db.collection("users").document(userId).update("ProfileImg", myUri)
                }
            }
        }
    }

    fun LoadImage() {
        val userId: String = fAuth.currentUser!!.uid
        db.collection("users").document(userId).get().addOnSuccessListener {
            if (it.exists()) {
                if (!it.getString("ProfileImg").equals("")) {
                    Picasso.get().load(it.getString("ProfileImg")).into(image)
                }
            }
        }
    }

    fun updateProfile() {
        updateprofile.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.update_profile, null)
            dialog.setContentView(view)
            dialog.show()

            view.back.setOnClickListener {
                dialog.dismiss()
            }

            view.upd_name.hint = prof_name.text
            view.upd_email.hint = prof_email.text
            view.upd_phone.hint = prof_phone.text

            view.savechanges.setOnClickListener {
                val userId: String = fAuth.currentUser!!.uid
                var check = false
                var check_name_error = true
                //update name
                if (view.upd_name.text.toString() != "" && view.upd_name.text.trim().length > 5) {
                    check = true
                } else if (view.upd_name.text.toString() != "" && view.upd_name.text.trim().length < 6) {
                    view.upd_name.error = "Name must have at least 6 characters"
                    check_name_error = false
                }

                //update email
                if (check_name_error) {
                    if (view.upd_email.text.toString() != "") {
                        if (Patterns.EMAIL_ADDRESS.matcher(view.upd_email.text).matches()) {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle("Enter Password")
                            val view2 = layoutInflater.inflate(R.layout.forgot_password, null)
                            view2.namepass.hint = "Enter password"
                            view2.namepass.transformationMethod =
                                PasswordTransformationMethod.getInstance()
                            builder.setView(view2)
                            builder.setPositiveButton(
                                "Save",
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    val credential = EmailAuthProvider.getCredential(
                                        fAuth.currentUser!!.email.toString(),
                                        view2.namepass.text.toString()
                                    )
                                    fAuth.currentUser!!.reauthenticate(credential)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                db.collection("users").document(userId)
                                                    .update("Email", view.upd_email.text.toString())
                                                fAuth.currentUser!!.updateEmail(view.upd_email.text.toString())
                                                if (check) {
                                                    db.collection("users").document(userId)
                                                        .update(
                                                            "Name",
                                                            view.upd_name.text.toString()
                                                        )
                                                }
                                                //update phone
                                                if (view.upd_phone.text.toString() != "") {
                                                    db.collection("users").document(userId)
                                                        .update(
                                                            "Phone",
                                                            view.upd_phone.text.toString()
                                                        )
                                                }
                                            }
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "Wrong Password!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            view.upd_email.error = "Wrong password!"
                                        }.addOnSuccessListener {
                                            dialog.dismiss()
                                            startActivity(intent)
                                            finish()
                                            overridePendingTransition(0, 0)
                                            Toast.makeText(
                                                this,
                                                "Profile updated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                })
                            builder.setNegativeButton(
                                "Close",
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                })
                            builder.show()
                        } else {
                            view.upd_email.error = "Wrong email"
                        }
                    } else {
                        if (check) {
                            db.collection("users").document(userId)
                                .update(
                                    "Name",
                                    view.upd_name.text.toString()
                                )
                        }
                        //update phone
                        if (view.upd_phone.text.toString() != "") {
                            db.collection("users").document(userId)
                                .update(
                                    "Phone",
                                    view.upd_phone.text.toString()
                                )
                        }
                        dialog.dismiss()
                        startActivity(intent)
                        finish()
                        overridePendingTransition(0, 0)
                        Toast.makeText(
                            this,
                            "Profile updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun changePassword() {
        changepassword.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = ChangePasswordFragment()
            fragmentTransaction.add(R.id.usertoplayout, fragment).addToBackStack("tag")
            fragmentTransaction.commit()

        }
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "fadein-to-fadeout")
    }
}


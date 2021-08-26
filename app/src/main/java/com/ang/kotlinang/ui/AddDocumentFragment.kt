package com.ang.kotlinang.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ang.kotlinang.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_document.*
import maes.tech.intentanim.CustomIntent

class AddDocumentFragment : Fragment(R.layout.fragment_add_document) {
    val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val strtext: String = arguments!!.getString("edttext")!!
        var counter: Int = arguments!!.getInt("Counter")
        val userID = fAuth.currentUser!!.uid
        addDocument(db, userID, strtext, counter)
        back.setOnClickListener {
            startActivity(Intent(activity, VocabularyListActivity::class.java))
            CustomIntent.customType(activity, "fadein-to-fadeout")
        }
    }

    fun addDocument(db: FirebaseFirestore, userID: String, colname: String, counter: Int) {
        adddoc.setOnClickListener {
            if (word_name.length() < 1) {
                word_name.error = "Type a word to translate"
            } else if (word_translation.length() < 1) {
                word_translation.error = "Type a word translation"
            } else {
                val map = HashMap<String, Any>()
                map["WordtoTranslate"] = word_name.text.trim().toString()
                map["Translation"] = word_translation.text.trim().toString()
                map["Note"] = word_description.text.trim().toString()
                db.collection("users").document(userID).collection("Collections").document(colname)
                    .collection("Words").document(word_name.text.trim().toString()).set(map)
                    .addOnSuccessListener {
                        word_description.text.clear()
                        word_translation.text.clear()
                        word_name.text.clear()
                        val reassign = counter + 1
                        Toast.makeText(activity, "Added successfully", Toast.LENGTH_SHORT).show()
                        db.collection("users").document(userID).collection("Collections")
                            .document(colname).update("Counter", reassign)
                    }.addOnFailureListener {
                        Toast.makeText(activity, "Not added", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
package com.ang.kotlinang.ui

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ang.kotlinang.R
import com.ang.kotlinang.model.Vocabulary
import com.ang.kotlinang.model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_vocabulary.*
import maes.tech.intentanim.CustomIntent


class VocabularyActivity : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    lateinit var fragment: Any
    var list = ArrayList<Word>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary)
        bottom_navigation.selectedItemId = R.id.page_3
        db = FirebaseFirestore.getInstance()
        fAuth = FirebaseAuth.getInstance()
        userID = fAuth.currentUser!!.uid
        val intent = intent
        val obj = intent.getParcelableExtra<Vocabulary>("Vocabulary Item")
        val name = obj!!.Name
        val description = obj.Description
        main_name.text = obj.Name
        voc_name.text = obj.Name
        voc_description.text = obj.Description
        voc_counter.text = obj.Counter.toString()
        val x = obj.Counter
        navClick(obj)

        back.setOnClickListener {
            startActivity(Intent(applicationContext, VocabularyListActivity::class.java))
            CustomIntent.customType(this, "fadein-to-fadeout")
        }

    }

    fun navClick(obj: Vocabulary) {
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.page_2 -> {
                    for (fragment in supportFragmentManager.fragments) {
                        supportFragmentManager.beginTransaction().remove(fragment).commit()
                    }
                    addDocument(obj)
                    true
                }
                R.id.page_3 -> {
                    for (fragment in supportFragmentManager.fragments) {
                        supportFragmentManager.beginTransaction().remove(fragment).commit()
                    }
                    true
                }
                R.id.page_4 -> {
                    for (fragment in supportFragmentManager.fragments) {
                        supportFragmentManager.beginTransaction().remove(fragment).commit()
                    }
                    loadDocuments(db, userID, obj.Name!!, 1)
                    true
                }
                R.id.page_5 -> {
                    for (fragment in supportFragmentManager.fragments) {
                        supportFragmentManager.beginTransaction().remove(fragment).commit()
                    }
                    loadDocuments(db, userID, obj.Name!!, 2)
                    true
                }
                else -> false
            }
        }
    }

    fun addDocument(obj: Vocabulary) {
        val bundle = Bundle()
        bundle.putString("edttext", obj.Name)
        bundle.putInt("Counter", obj.Counter!!)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AddDocumentFragment()
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.voctoplayout, fragment).addToBackStack("tag")
        fragmentTransaction.commit()
    }

    fun searchDocument(list: ArrayList<Word>, fragm: Int, doc: String) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(
            "docs",
            list as java.util.ArrayList<out Parcelable>
        )
        bundle.putString("Name", doc)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragment = if (fragm == 1) {
            SearchForDocumentFragment()
        } else {
            EngToPLFragment()
        }
        (fragment as Fragment).arguments = bundle
        fragmentTransaction.add(R.id.voctoplayout, fragment as Fragment).addToBackStack("tag")
        fragmentTransaction.commit()
    }

//    fun loadDocuments(db: FirebaseFirestore, userID: String, col: String): ArrayList<Word> {
//        val listfb = ArrayList<Word>()
//        db.collection("users").document(userID)
//            .collection("Collections")//.document(col).collection("Words")
//            .addSnapshotListener(this, object :
//                EventListener<QuerySnapshot?> {
//                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//                    if (error != null) {
//                        return
//                    }
//                    list.clear()
//                    for (documentSnapshot in value!!) {
//                        val voc: Word = documentSnapshot.toObject(Word::class.java)
//                        listfb.add(voc)
//                    }
//
//                }
//            })
//        return listfb
//    }

    fun loadDocuments(
        db: FirebaseFirestore,
        userID: String,
        doc: String,
        fragm: Int
    ): ArrayList<Word> {
        val listfb = ArrayList<Word>()
        db.collection("users").document(userID).collection("Collections").document(doc)
            .collection("Words")
            .addSnapshotListener(this, object :
                EventListener<QuerySnapshot?> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }
                    list.clear()
                    for (documentSnapshot in value!!) {
                        val voc = documentSnapshot.toObject(Word::class.java)
                        listfb.add(voc)
                    }
                    searchDocument(listfb, fragm, doc)
                }
            })
        return listfb
    }
}
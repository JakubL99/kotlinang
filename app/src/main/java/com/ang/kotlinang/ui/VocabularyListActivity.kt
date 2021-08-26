package com.ang.kotlinang.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ang.kotlinang.R
import com.ang.kotlinang.adapter.VocAdapter
import com.ang.kotlinang.model.Vocabulary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_vocabularylist.*
import kotlinx.android.synthetic.main.add_collection.view.*


class VocabularyListActivity : AppCompatActivity(), VocAdapter.onVocabularyListener {
    lateinit var fAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var adapter: VocAdapter
    var list = ArrayList<Vocabulary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabularylist)
        setSupportActionBar(toolbar2)
        title = "Vocabulary"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userID = fAuth.currentUser!!.uid
        list = loadCollections(db, userID)
        addMenu(db, userID)
        scroll()
    }

    fun addMenu(db: FirebaseFirestore, userID: String) {
        addCollection(db, userID)
    }

    fun addCollection(db: FirebaseFirestore, userID: String) {
        add.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Collection")
            val v = layoutInflater.inflate(R.layout.add_collection, null)
            builder.setView(v)
            builder.setPositiveButton(
                "Create"
            ) { _, _ ->

            }
                .setNegativeButton("Cancel") { _, _ ->
                }

            val dialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val map = HashMap<String, Any>()
                if (!v.addcol.text.trim().toString().equals("") && v.addcol.length() > 2) {
                    map["Name"] = v.addcol.text.trim().toString()
                    map["Description"] = v.desciption.text.trim().toString()
                    map["FirstName"] = v.addcol.text.trim().toString()
                    map["Counter"] = 0
                    db.collection("users").document(userID)
                        .collection("Collections").document(v.addcol.text.trim().toString())
                        .set(map)
                    Toast.makeText(this, "Collection created", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    floatingActionsMenu.collapse()
                } else {
                    v.addcol.error =
                        "Enter collection name. Length must be greater than 2 characters"
                }
            }
        }
    }

    fun scroll() {
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && floatingActionsMenu.isShown) {
                    floatingActionsMenu.collapse()
                    floatingActionsMenu.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) floatingActionsMenu.visibility =
                    View.VISIBLE
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun setUpRecyclerView(list: ArrayList<Vocabulary>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        adapter = VocAdapter(list, this, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    fun loadCollections(db: FirebaseFirestore, userID: String): ArrayList<Vocabulary> {
        val listfb = ArrayList<Vocabulary>()
        db.collection("users").document(userID).collection("Collections")
            .addSnapshotListener(this, object :
                EventListener<QuerySnapshot?> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }
                    list.clear()
                    for (documentSnapshot in value!!) {
                        val voc = documentSnapshot.toObject(Vocabulary::class.java)
                        listfb.add(voc)
                    }
                    setUpRecyclerView(listfb)
                }
            })
        return listfb
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.voc_menu, menu)
        val searchitem = menu?.findItem(R.id.action_search)
        val searchView: SearchView = searchitem?.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })
        return true
    }

    override fun onVocabularyClick(position: Int) {
        val intent = Intent(this, VocabularyActivity::class.java)
        intent.putExtra("Vocabulary Item", list[position])
        startActivity(intent)
    }


}

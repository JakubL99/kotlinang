package com.ang.kotlinang.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ang.kotlinang.R
import com.ang.kotlinang.adapter.DocAdapter
import com.ang.kotlinang.model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_search_for_document.*
import kotlinx.android.synthetic.main.fragment_search_for_document.view.*


class SearchForDocumentFragment : Fragment(R.layout.fragment_search_for_document) {
    lateinit var fAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var adapter: DocAdapter
    var list = ArrayList<Word>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        fAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userID = fAuth.currentUser!!.uid
        toolbar2.inflateMenu(R.menu.voc_menu)
        view.toolbar2.title = "Words"
        list = arguments!!.getParcelableArrayList<Word>("docs") as ArrayList<Word>
        val docname = arguments!!.getString("Name")
        setUpRecyclerView(list, docname!!)

    }

    private fun setUpRecyclerView(list: ArrayList<Word>, doc: String) {
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view_doc)
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        adapter = DocAdapter(list, context!!, doc)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.voc_menu, menu)
        val searchitem = menu.findItem(R.id.action_search)
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
        super.onCreateOptionsMenu(menu, inflater)
    }
}
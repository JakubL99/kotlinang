package com.ang.kotlinang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ang.kotlinang.R
import com.ang.kotlinang.model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class DocAdapter internal constructor(
    exampleList: ArrayList<Word>,
    private var context: Context,
    doc: String
) :
    RecyclerView.Adapter<DocAdapter.DocViewHolder>(), Filterable {
    private val exampleList: List<Word?>
    private val exampleListFull: List<Word?>
    private val doc: String

    init {
        this.exampleList = exampleList
        this.exampleListFull = ArrayList<Word>(exampleList)
        this.doc = doc
    }

    class DocViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.doc_name)
        val desc: TextView = itemView.findViewById(R.id.doc_desc)
        val edit: ImageView = itemView.findViewById(R.id.doc_edit)
        val del: ImageView = itemView.findViewById(R.id.doc_del)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.doc_layout, parent, false)
        return DocViewHolder(v)
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        val currentItem: Word? = exampleList[position]
        holder.name.text = currentItem!!.WordtoTranslate
        holder.desc.text = currentItem.Translation
        val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userID = fAuth.currentUser!!.uid

//        holder.edit.setOnClickListener {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("Edit Collection")
//            val v = LayoutInflater.from(context).inflate(R.layout.add_collection, null)
//            builder.setView(v)
//            builder.setPositiveButton(
//                "Edit"
//            ) { _, _ ->
//
//            }
//                .setNegativeButton(
//                    "Cancel"
//                ) { _, _ ->
//                }
//
//            val dialog = builder.create()
//            dialog.show()
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
//                val map = HashMap<String, String>()
//                if (!v.addcol.text.trim().toString().equals("") && v.addcol.length() > 2) {
//                    map["Name"] = v.addcol.text.trim().toString()
//                    map["Description"] = v.desciption.text.trim().toString()
//                    db.collection("users").document(userID)
//                        .collection("Collections")
//                        .document(currentItem.FirstName.toString())
//                        .update(map as Map<String, String>)
//                    Toast.makeText(context, "Collection updated", Toast.LENGTH_SHORT).show()
//                    dialog.dismiss()
//                } else {
//                    v.addcol.error =
//                        "Enter collection name. Length must be greater than 2 characters"
//                }
//            }
//
//        }
        holder.del.setOnClickListener {
            db.collection("users").document(userID).collection("Collections")
                .document(doc).collection("Words").document(currentItem.toString()).delete()
        }
    }

    override fun getItemCount(): Int {
        return exampleList.size
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<Word?> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(exampleListFull)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                for (item in exampleListFull) {
                    if (item!!.WordtoTranslate!!.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            exampleList.clear()
            exampleList.addAll((results.values as ArrayList<Word>))
            notifyDataSetChanged()
        }
    }


}
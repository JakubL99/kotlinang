package com.ang.kotlinang.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ang.kotlinang.R
import com.ang.kotlinang.model.Vocabulary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_collection.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class VocAdapter internal constructor(
    exampleList: ArrayList<Vocabulary>,
    private var context: Context, onVocabularyListener: onVocabularyListener
) :
    RecyclerView.Adapter<VocAdapter.ExampleViewHolder>(), Filterable {
    private val exampleList: List<Vocabulary?>
    private val exampleListFull: List<Vocabulary?>
    private val mOnVocabularyListener: onVocabularyListener

    init {
        this.exampleList = exampleList
        this.exampleListFull = ArrayList<Vocabulary>(exampleList)
        this.mOnVocabularyListener = onVocabularyListener
    }

    class ExampleViewHolder(itemView: View, onVocabularyListener: onVocabularyListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val onVocabularyListener: onVocabularyListener
        val name: TextView = itemView.findViewById(R.id.card_name)
        val desc: TextView = itemView.findViewById(R.id.card_desc)
        val edit: ImageView = itemView.findViewById(R.id.rec_edit)
        val del: ImageView = itemView.findViewById(R.id.rec_del)

        init {
            itemView.setOnClickListener(this)
            this.onVocabularyListener = onVocabularyListener
        }

        override fun onClick(p0: View?) {
            onVocabularyListener.onVocabularyClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ExampleViewHolder(v, mOnVocabularyListener)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem: Vocabulary? = exampleList[position]
        holder.name.text = currentItem!!.Name
        holder.desc.text = currentItem.Description
        val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userID = fAuth.currentUser!!.uid

        holder.edit.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Edit Collection")
            val v = LayoutInflater.from(context).inflate(R.layout.add_collection, null)
            builder.setView(v)
            builder.setPositiveButton(
                "Edit"
            ) { _, _ ->

            }
                .setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                }

            val dialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val map = HashMap<String, String>()
                if (!v.addcol.text.trim().toString().equals("") && v.addcol.length() > 2) {
                    map["Name"] = v.addcol.text.trim().toString()
                    map["Description"] = v.desciption.text.trim().toString()
                    db.collection("users").document(userID)
                        .collection("Collections")
                        .document(currentItem.FirstName.toString())
                        .update(map as Map<String, String>)
                    Toast.makeText(context, "Collection updated", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    v.addcol.error =
                        "Enter collection name. Length must be greater than 2 characters"
                }
            }

        }
        holder.del.setOnClickListener {
            db.collection("users").document(userID).collection("Collections")
                .document(currentItem.FirstName.toString()).delete()
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
            val filteredList: MutableList<Vocabulary?> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(exampleListFull)
            } else {
                val filterPattern =
                    constraint.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }
                for (item in exampleListFull) {
                    if (item!!.Name!!.toLowerCase(Locale.ROOT).contains(filterPattern)) {
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
            exampleList.addAll((results.values as ArrayList<Vocabulary>))
            notifyDataSetChanged()
        }
    }

    interface onVocabularyListener {
        fun onVocabularyClick(position: Int)
    }
}
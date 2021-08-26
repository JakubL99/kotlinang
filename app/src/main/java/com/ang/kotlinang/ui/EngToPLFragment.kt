package com.ang.kotlinang.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ang.kotlinang.R
import com.ang.kotlinang.model.Word
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_add_document.back
import kotlinx.android.synthetic.main.fragment_eng_to_pl.*
import maes.tech.intentanim.CustomIntent
import java.util.*
import kotlin.collections.ArrayList


class EngToPLFragment : Fragment(R.layout.fragment_eng_to_pl) {
    val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var list = ArrayList<Word>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = arguments!!.getParcelableArrayList<Word>("docs") as ArrayList<Word>
        val userID = fAuth.currentUser!!.uid

        back.setOnClickListener {
            startActivity(Intent(activity, VocabularyListActivity::class.java))
            CustomIntent.customType(activity, "fadein-to-fadeout")
        }
        val obj = displayDocument()
        nextWord()
        checkTranslation(obj)
        showTranslation(obj)
    }

    fun getRandomNumber(): Int {
        val random = Random()
        val number = random.nextInt(list.size)
        println(number)
        return number
    }

    fun displayDocument(): Word {
        val obj = list[getRandomNumber()]
        to_translate.text = obj.WordtoTranslate
        return obj
    }

    fun nextWord() {
        nextword.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            ft.detach(this).attach(this).commit()
            word_translation.text.clear()
        }
    }

    fun checkTranslation(obj: Word) {
        checkdoc.setOnClickListener {
            println("Obiekt " + obj.Translation)
            println("Textview " + word_translation.text)
            if (word_translation.length() < 1)
                word_translation.error = "Length less than 1!"
            else {
                val s1 = "" + word_translation.text
                if (s1 == obj.Translation) {
                    word_translation.setTextColor(Color.parseColor("#009900"))
                } else
                    word_translation.setTextColor(Color.parseColor("#ff0000"))
            }
        }
    }

    fun showTranslation(obj: Word) {
        showTranslation.setOnClickListener {
            word_translation.setText(obj.Translation)
        }
    }
}

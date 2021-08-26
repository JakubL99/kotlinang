package com.ang.kotlinang.model

import android.os.Parcel
import android.os.Parcelable

class Word() : Parcelable {
    var WordtoTranslate: String? = null
    var Translation: String? = null
    var Note: String? = null

    constructor(parcel: Parcel) : this() {
        WordtoTranslate = parcel.readString()
        Translation = parcel.readString()
        Note = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(WordtoTranslate)
        parcel.writeString(Translation)
        parcel.writeString(Note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }


}
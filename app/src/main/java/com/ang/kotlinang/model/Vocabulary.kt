package com.ang.kotlinang.model

import android.os.Parcel
import android.os.Parcelable

class Vocabulary() : Parcelable {
    var Name: String? = null
    var Description: String? = null
    var FirstName: String? = null
    var Counter: Int? = null

    constructor(parcel: Parcel) : this() {
        Name = parcel.readString()
        Description = parcel.readString()
        FirstName = parcel.readString()
        Counter = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Name)
        parcel.writeString(Description)
        parcel.writeString(FirstName)
        parcel.writeValue(Counter)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vocabulary> {
        override fun createFromParcel(parcel: Parcel): Vocabulary {
            return Vocabulary(parcel)
        }

        override fun newArray(size: Int): Array<Vocabulary?> {
            return arrayOfNulls(size)
        }
    }


}
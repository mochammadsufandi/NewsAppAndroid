package com.example.retrofitbeginner.model

import android.os.Parcel
import android.os.Parcelable

data class Source(
    val id: String?,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Source> {
        override fun createFromParcel(parcel: Parcel): Source = Source(parcel)
        override fun newArray(size: Int): Array<Source?> = arrayOfNulls(size)
        }
}
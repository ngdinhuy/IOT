package com.example.httm.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Student(val userName:String,val code:String, val exist:Boolean): Parcelable {
}
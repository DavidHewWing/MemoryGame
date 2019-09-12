package com.example.memorygameandroid

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardModel(val title: String? = "", val id: Long=0, val imageUrl: String? ="") : Parcelable
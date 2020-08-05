package com.promisebooks.app.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class Book(var title: String, var image: String, var description: String, var price: String) :
    Parcelable {
    constructor(): this(" "," ", " ", " ")
}
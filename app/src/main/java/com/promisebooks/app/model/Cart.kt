package com.promisebooks.app.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class Cart(var title: String, var image: String, var description: String, var price: String, var success: Boolean, var ref: String, var id: String, var name: String, var phone: String, var qty: Int) :
    Parcelable {
    constructor(): this(" "," ", " ", " ", false, "", "", "", "", 0)
}
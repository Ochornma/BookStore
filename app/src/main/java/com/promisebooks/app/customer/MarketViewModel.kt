package com.promisebooks.app.customer

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.model.Book
import com.promisebooks.app.model.Cart
import com.promisebooks.app.util.CartCallback
import java.text.SimpleDateFormat
import java.util.*

class MarketViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var collectionCart = db.collection("Cart")

    fun getTime(): String{
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH)
        return sdf.format(c.time)
    }

    fun cartCollection(book: Book, email: String, ref: String, uiid: String, name: String, phone: String, callback: CartCallback){
        collectionCart.document("${book.title}_$email").get().addOnSuccessListener {
            if (it.exists()){
                val cart1 = it.toObject(Cart::class.java)
                val qty = cart1?.qty
                val cart = Cart(book.title, book.image, book.description, book.price, false, ref,
                    uiid, name, phone,
                    qty?.plus(1)!!
                )
                collectionCart.document("${book.title}_$email").set(cart).addOnSuccessListener {
                    callback.callback()

                }
            } else{
                val cart = Cart(book.title, book.image, book.description, book.price, false, ref,
                    uiid, name, phone, 1)
                collectionCart.document("${book.title}_$email").set(cart).addOnSuccessListener {
                    callback.callback()


                }
            }
        }
    }
}
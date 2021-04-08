package com.promisebooks.app.customer

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.model.BookBought
import com.promisebooks.app.util.CartBookCallback
import java.util.ArrayList

class MyBookViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var collectionProduct = db.collection("BoughtProducts")

    fun getBook(uiid: String, callback: CartBookCallback){
        val books: MutableList<BookBought> = ArrayList()
        collectionProduct.whereEqualTo("id", uiid).get().addOnSuccessListener {
            if (!it.isEmpty){

                val list = it.documents
                for (item in list){
                    val book = item.toObject(BookBought::class.java)
                    if (book != null) {
                        books.add(book)
                    }
                }
                callback.callback(books)

            } else{
                callback.callbackError()


            }
        }
    }
}
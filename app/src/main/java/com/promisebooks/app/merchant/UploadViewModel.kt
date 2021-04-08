package com.promisebooks.app.merchant

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.promisebooks.app.model.Book
import com.promisebooks.app.util.CartCallback

class UploadViewModel : ViewModel() {

    private var mStorageRef: StorageReference? = null
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Books")

    init {
        mStorageRef = FirebaseStorage.getInstance().reference
    }

    fun uploadImage(imageurl: Uri, title: String, descripe: String, price: String, callback: CartCallback){
        val riversRef: StorageReference = mStorageRef?.child("flutterwave/${System.currentTimeMillis()}")!!
        riversRef.putFile(imageurl).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {it1 ->
                val book = Book(title, it1.toString(), descripe, price)
                collection.document().set(book).addOnSuccessListener {
                    callback.callback()
                }
            }

        }.addOnFailureListener {
            callback.callbackError()
        }
    }
}
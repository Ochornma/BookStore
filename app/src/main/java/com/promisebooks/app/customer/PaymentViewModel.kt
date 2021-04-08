package com.promisebooks.app.customer

import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.model.AccounRequest
import com.promisebooks.app.model.Book
import com.promisebooks.app.model.BookBought
import com.promisebooks.app.model.USSDRequest
import com.promisebooks.app.util.*
import java.text.SimpleDateFormat
import java.util.*

class PaymentViewModel(
    accountRecieved: AccountRecieved,
    ussdRecieved: Transaction,
    context: Context,
    progressCheck: ProgressCheck
) : ViewModel() {

    private var db = FirebaseFirestore.getInstance()

    private var collectionProduct = db.collection("BoughtProducts")

    private var repo: BookRepo = BookRepo(accountRecieved, ussdRecieved, context, progressCheck)

    fun sendUssd(ussdRequest: USSDRequest) {
        repo.sendUSSD(ussdRequest)
    }

    fun sendBank(accounRequest: AccounRequest) {
        repo.sendTransfer(accounRequest)
    }

    fun getBank() {
        repo.getBanks()
    }

    fun getProgress(url: String, price : String) {
        repo.getTransaction(url, price, true)
    }

    fun getTime(): String{
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH)
        return sdf.format(c.time)
    }

    fun getData(book: Book, ref: String, uiid: String, callback: CartCallback){
        val bookBought = BookBought(book.title, book.image, book.description, book.price, uiid)
        collectionProduct.document(ref).set(bookBought).addOnSuccessListener {
            callback.callback()

        }
    }

}
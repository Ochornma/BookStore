package com.promisebooks.app.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.model.AccounRequest
import com.promisebooks.app.model.BookBought
import com.promisebooks.app.model.Cart
import com.promisebooks.app.model.USSDRequest
import com.promisebooks.app.util.*
import java.text.SimpleDateFormat
import java.util.*

class CartPaymentViewModel(accountRecieved: AccountRecieved, ussdRecieved: Transaction, context: Context, progressCheck: ProgressCheck) : ViewModel() {
    private var repo: BookRepo = BookRepo(accountRecieved, ussdRecieved, context, progressCheck)
    private var db = FirebaseFirestore.getInstance()
    private var collectionUser = db.collection("Users")
    private var collectionProduct = db.collection("BoughtProducts")
    private var collectionCart = db.collection("Cart")

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

    fun setData(cart: Cart, ref: String, uiid: String, email: String, callback: CartDeleteCalback){
        val bookBought = BookBought(cart.title, cart.image, cart.description, cart.price, uiid)
        collectionProduct.document(ref).set(bookBought).addOnSuccessListener {
            collectionCart.document("${cart.title}_$email").delete().addOnSuccessListener {
                callback.deleteCallback()


            }

        }
    }

    fun getTime(): String{
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH)
        return sdf.format(c.time)
    }
}
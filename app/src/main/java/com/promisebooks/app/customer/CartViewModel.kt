package com.promisebooks.app.customer

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.model.Cart
import com.promisebooks.app.model.Refund
import com.promisebooks.app.util.CartDeleteCalback
import com.promisebooks.app.util.CartRefundCallback
import java.util.ArrayList

class CartViewModel : ViewModel() {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var collectionCart: CollectionReference
    private var collectionRefund: CollectionReference


    init {
        collectionCart = db.collection("Cart")
        collectionRefund = db.collection("Refund")
    }

    fun getCart(uiid: String): MutableList<Cart> {
        val carts: MutableList<Cart> = ArrayList()
        collectionCart.whereEqualTo("id", uiid).get().addOnSuccessListener {
            if (!it.isEmpty) {

                val list = it.documents
                for (item in list) {
                    val cart = item.toObject(Cart::class.java)
                    if (cart != null) {
                        carts.add(cart)
                    }
                }
            }

        }
        return carts
    }

    fun deleteCart(cart: Cart, email: String, callback: CartDeleteCalback ){
        collectionCart.document("${cart.title}_$email").delete().addOnSuccessListener {
           // Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
           // getData()
            callback.deleteCallback()
        }
    }

    fun refundcart(refund: Refund, cart: Cart, email: String, refundCallback: CartRefundCallback){
        collectionRefund.document().set(refund).addOnSuccessListener {
            collectionCart.document("${cart.title}_$email").delete().addOnSuccessListener {
                refundCallback.refundCallback()
            }
        }
    }
}
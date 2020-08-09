package com.promisebooks.app.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.promisebooks.app.model.AccounRequest
import com.promisebooks.app.model.USSDRequest
import com.promisebooks.app.util.AccountRecieved
import com.promisebooks.app.util.BookRepo
import com.promisebooks.app.util.ProgressCheck
import com.promisebooks.app.util.Transaction

class CartPaymentViewModel(val accountRecieved: AccountRecieved, val ussdRecieved: Transaction, context: Context, progressCheck: ProgressCheck) : ViewModel() {
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
}
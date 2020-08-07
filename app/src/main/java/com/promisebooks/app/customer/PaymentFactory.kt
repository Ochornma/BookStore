package com.promisebooks.app.customer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.promisebooks.app.util.AccountRecieved
import com.promisebooks.app.util.ProgressCheck
import com.promisebooks.app.util.Transaction

class PaymentFactory(val accountRecieved: AccountRecieved, val ussdRecieved: Transaction, val context: Context, val progressCheck: ProgressCheck): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(accountRecieved, ussdRecieved, context, progressCheck) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
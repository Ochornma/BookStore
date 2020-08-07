package com.promisebooks.app.merchant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.promisebooks.app.util.DetailRecieved

class TransactionFactory(val accountRecieved: DetailRecieved, val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewViewModel::class.java)) {
            return TransactionViewViewModel(context, accountRecieved) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
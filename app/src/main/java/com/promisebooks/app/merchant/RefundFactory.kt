package com.promisebooks.app.merchant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.promisebooks.app.util.RefundVerify

class RefundFactory(val refund: RefundVerify, val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RefundViewModel::class.java)) {
            return RefundViewModel(refund, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
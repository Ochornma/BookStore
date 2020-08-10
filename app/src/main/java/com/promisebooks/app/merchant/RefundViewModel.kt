package com.promisebooks.app.merchant

import android.content.Context
import androidx.lifecycle.ViewModel
import com.promisebooks.app.util.BookRepo
import com.promisebooks.app.util.RefundVerify

class RefundViewModel(refundVerify: RefundVerify, context: Context) : ViewModel() {
    val repo = BookRepo(refundVerify, context)

    fun getTransact(ref: String){
        repo.getRefundTransaction(ref)
    }
}
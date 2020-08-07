package com.promisebooks.app.merchant

import android.content.Context
import androidx.lifecycle.ViewModel
import com.promisebooks.app.util.BookRepo
import com.promisebooks.app.util.DetailRecieved

class TransactionViewViewModel(val context: Context, val detailRecieved: DetailRecieved) : ViewModel() {
    private var repo: BookRepo = BookRepo(detailRecieved, context)

    fun transact(url: String){
        repo.getTransaction(url, "", false)
    }

}
package com.promisebooks.app.merchant

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.promisebooks.app.model.TransactionDetails
import com.promisebooks.app.util.BookRepo
import com.promisebooks.app.util.DetailRecieved


class TransactionViewViewModel(val context: Context, val detailRecieved: DetailRecieved, url: String) : ViewModel() {
    private var repo: BookRepo = BookRepo(detailRecieved, context, url)


    fun trans(): LiveData<PagedList<TransactionDetails>>{
        return repo.itemPagedList
    }



    fun transact(url: String){
        repo.getTransaction(url, "", false)
    }



}
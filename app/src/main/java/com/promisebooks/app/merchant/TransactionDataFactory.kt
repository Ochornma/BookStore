package com.promisebooks.app.merchant


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.promisebooks.app.model.TransactionDetails


class TransactionDataFactory(val context: Context, val url: String): DataSource.Factory<Int, TransactionDetails>() {

    private val itemLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, TransactionDetails>>()
    override fun create(): DataSource<Int, TransactionDetails> {
       val dataSource = TransactionDataSource(context, url)
        itemLiveDataSource.postValue(dataSource)

        return dataSource
    }

    fun getItemLiveDataSource(): MutableLiveData<PageKeyedDataSource<Int, TransactionDetails>> {
        return itemLiveDataSource
    }
}




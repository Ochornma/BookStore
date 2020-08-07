package com.promisebooks.app.util

import com.promisebooks.app.model.TransactionDetails

interface DetailRecieved {
    fun recieved(detail: MutableList<TransactionDetails>)
    fun error()
}
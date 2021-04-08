package com.promisebooks.app.util

import com.promisebooks.app.model.Book
import com.promisebooks.app.model.BookBought

interface CartCallback {
    fun callback()
    fun callbackError()
}

interface CartBookCallback{
    fun callback(book: MutableList<BookBought>)
    fun callbackError()
}
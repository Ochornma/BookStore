package com.promisebooks.app.util

import com.promisebooks.app.model.Data1

interface AccountRecieved {
    fun recieved(banks: List<Data1>)
    fun errorAccount()
}
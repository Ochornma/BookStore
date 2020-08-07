package com.promisebooks.app.util

interface Transaction {
    fun received(code: String, ref: String)
    fun error()
}
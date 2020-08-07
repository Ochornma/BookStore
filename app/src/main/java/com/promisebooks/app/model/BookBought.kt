package com.promisebooks.app.model

data class BookBought(var title: String, var image: String, var description: String, var price: String, var id: String) {
    constructor(): this("", "", "", "", "")
}
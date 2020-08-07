package com.promisebooks.app.model

data class User(var name: String, var phone: String) {

    constructor(): this("", "")
}
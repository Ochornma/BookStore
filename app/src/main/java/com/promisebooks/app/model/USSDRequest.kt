package com.promisebooks.app.model

data class USSDRequest(
    val account_bank: String,
    val amount: String,
    val currency: String,
    val email: String,
    val fullname: String,
    val phone_number: String,
    val tx_ref: String
)
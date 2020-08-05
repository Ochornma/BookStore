package com.promisebooks.app.model

data class Registeration(
    val amount: String,
    val currency: String,
    val customer: Customer,
    val customizations: Customizations,
    val meta: Meta,
    val payment_options: String,
    val redirect_url: String,
    val tx_ref: String
)

data class Customer(
    val email: String,
    val name: String,
    val phonenumber: String
)

data class Customizations(
    val description: String,
    val logo: String,
    val title: String
)

data class Meta(
    val consumer_id: Int,
    val consumer_mac: String
)
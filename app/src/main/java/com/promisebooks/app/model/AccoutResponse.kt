package com.promisebooks.app.model

data class AccoutResponse(
    val message: String,
    val meta: MetaX,
    val status: String
)

data class MetaX(
    val authorization: Authorization
)

data class Authorization(
    val account_expiration: String,
    val mode: String,
    val transfer_account: String,
    val transfer_amount: Int,
    val transfer_bank: String,
    val transfer_note: String,
    val transfer_reference: String
)
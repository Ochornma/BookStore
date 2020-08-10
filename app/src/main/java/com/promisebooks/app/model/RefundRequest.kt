package com.promisebooks.app.model

data class RefundRequest(
    val amount: Int,
    val flw_ref: String
){
    constructor():this(0, "")
}
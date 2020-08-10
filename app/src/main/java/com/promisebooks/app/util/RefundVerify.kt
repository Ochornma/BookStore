package com.promisebooks.app.util

import com.promisebooks.app.model.RefundRequest

interface RefundVerify {
    fun verified(id: Int, refundRequest: RefundRequest)
    fun Refunderror()
}
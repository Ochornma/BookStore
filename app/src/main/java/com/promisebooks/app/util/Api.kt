package com.promisebooks.app.util

import com.promisebooks.app.model.*
import com.squareup.okhttp.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK-2047ac195430e50a46398fc9910921cb-X"
    )
    @POST("charges?type=ussd")
    fun sendUSSDRequest(@Body ussdRequest: USSDRequest) : Call<USSDResponse>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK-2047ac195430e50a46398fc9910921cb-X"
    )
    @POST("charges?type=bank_transfer")
    fun sendBankRequest(@Body bankRequest: AccounRequest): Call<AccoutResponse>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK-2047ac195430e50a46398fc9910921cb-X"
    )
    @POST("payments")
    fun sendRegister(@Body regRequest: Registeration): Call<RegisterationResponse>



    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK-2047ac195430e50a46398fc9910921cb-X"
    )
    @GET("transactions")
    fun getTransaction
                (@Query("from") from: String,
                 @Query("to") to: String,
    @Query("tx_ref") ref: String): Call<ResponseBody>



    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK-2047ac195430e50a46398fc9910921cb-X"
    )
    @GET("transactions")
    fun getAllTransaction
                (@Query("from") from: String,
                 @Query("to") to: String): Call<ResponseBody>
}
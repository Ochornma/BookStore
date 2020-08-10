package com.promisebooks.app.util

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.promisebooks.app.model.*
import com.squareup.okhttp.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface Api {
    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK_TEST-d99052ada1e92bfaef644579c8585b83-X"
    )
    @POST("charges?type=ussd")
    fun sendUSSDRequest(@Body ussdRequest: USSDRequest): Call<USSDResponse>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK_TEST-d99052ada1e92bfaef644579c8585b83-X"
    )
    @POST("charges?type=bank_transfer")
    fun sendBankRequest(@Body bankRequest: AccounRequest): Call<AccoutResponse>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK_TEST-d99052ada1e92bfaef644579c8585b83-X"
    )
    @POST("payments")
    fun sendRegister(@Body regRequest: Registeration): Call<RegisterationResponse>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK_TEST-d99052ada1e92bfaef644579c8585b83-X"
    )
    @GET("transactions")
    fun getTransaction(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("tx_ref") ref: String
    ): Call<ResponseBody>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK_TEST-d99052ada1e92bfaef644579c8585b83-X"
    )
    @GET("transactions")
    fun getAllTransaction(
        @Query("from") from: String,
        @Query("to") to: String
    ): Call<ResponseBody>


    @Headers(
        "Content-type: application/json",
        "Authorization: Bearer FLWSECK_TEST-d99052ada1e92bfaef644579c8585b83-X"
    )
    @GET("banks/NG")
    fun getBankCode(): Call<Banks>
}


class RetrofitClient {

    private val url = "https://api.flutterwave.com/v3/"
    private var mInstance: RetrofitClient? = null
    private var retrofit: Retrofit? = null


    init {
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Synchronized
    fun getInstance(): RetrofitClient {
        if (mInstance == null) {
            mInstance = RetrofitClient()
        }
        return mInstance!!
    }

    fun getApi(): Api {
        return retrofit!!.create(Api::class.java)
    }
}

object VolleyRequest {
    private var volley: RequestQueue? = null

    @Synchronized
    fun getVolley(context: Context): RequestQueue? {
        if (volley == null) {
            volley = Volley.newRequestQueue(context.applicationContext)
        }
        return volley
    }
}
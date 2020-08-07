package com.promisebooks.app.util

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.promisebooks.app.model.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class BookRepo {
    private lateinit var ussdRecieved: Transaction
    private lateinit var detailRecieved: DetailRecieved
    private lateinit var accountRecieved: AccountRecieved
    private lateinit var context: Context
    private lateinit var progressCheck: ProgressCheck
    private var mQueue: RequestQueue? = null

    constructor(detail: DetailRecieved, context: Context){
        this.detailRecieved = detail
        mQueue = VolleyRequest.getVolley(context)
    }

    constructor(accountRecieved: AccountRecieved, ussdRecieved: Transaction, context: Context, progressCheck: ProgressCheck){
        this.accountRecieved = accountRecieved
        this.ussdRecieved = ussdRecieved
        this.context = context
        this.progressCheck = progressCheck
        mQueue = VolleyRequest.getVolley(context)
    }


    fun sendUSSD(ussdRequest: USSDRequest){
    val call: Call<USSDResponse> = RetrofitClient().getInstance().getApi().sendUSSDRequest(ussdRequest)
        call.enqueue(object : Callback<USSDResponse> {
            override fun onFailure(call: Call<USSDResponse>, t: Throwable) {
              ussdRecieved.error()
            }

            override fun onResponse(call: Call<USSDResponse>, response: Response<USSDResponse>) {
                val response1 = response.body()?.meta?.authorization?.note
                val responseUse = "Dial +$response1 to complete Payment"
                val ref = response.body()?.data?.tx_ref
                if (ref != null) {
                    ussdRecieved.received(responseUse, ref)
                }
            }

        })
    }

    fun sendTransfer(accounRequest: AccounRequest){
        val call: Call<AccoutResponse> = RetrofitClient().getInstance().getApi().sendBankRequest(accounRequest)
        call.enqueue(object : Callback<AccoutResponse> {
            override fun onFailure(call: Call<AccoutResponse>, t: Throwable) {
                ussdRecieved.error()
            }

            override fun onResponse(
                call: Call<AccoutResponse>,
                response: Response<AccoutResponse>
            ) {
                val response1 = response.body()?.meta?.authorization?.transfer_note
                val response2 = response.body()?.meta?.authorization?.transfer_account
                val response3 = response.body()?.meta?.authorization?.transfer_amount
                val response4 = response.body()?.meta?.authorization?.transfer_bank
                val response5 = response.body()?.meta?.authorization?.transfer_reference
                val responseUse =
                    "$response1\nAccount Number: $response2\nAmount: $response3\nBank: $response4"
                if (response5 != null) {
                    ussdRecieved.received(responseUse, response5)
                }
            }

        })
    }


    fun getBanks(){
        val call: Call<Banks> = RetrofitClient().getInstance().getApi().getBankCode()
        call.enqueue(object : Callback<Banks> {
            override fun onFailure(call: Call<Banks>, t: Throwable) {
           accountRecieved.error()
            }

            override fun onResponse(call: Call<Banks>, response: Response<Banks>) {
                if (response.isSuccessful){
                    val data1: List<Data1> = response.body()?.data!!
                    accountRecieved.recieved(data1)
                }

            }

        })
    }


    fun getTransaction(url: String, price: String, single: Boolean) {
        val detail: MutableList<TransactionDetails> = ArrayList<TransactionDetails>()
        val request: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.GET, url, null,
                com.android.volley.Response.Listener { response: JSONObject ->
                    try {
                        if (single){
                            val data = response.getJSONArray("data")
                            val properties = data.getJSONObject(0)
                            val amount = properties.getString("charged_amount")
                            val status = properties.getString("status")
                            if ((amount == price) and (status == "successful")){
                                progressCheck.progress()
                            }else{
                                progressCheck.error()
                            }
                        }else{
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()){
                                val properties = data.getJSONObject(i)
                                val amount = properties.getString("charged_amount")
                                val status = properties.getString("status")
                                val id = properties.get("id")
                                val type = properties.getString("payment_type")
                                val date = properties.getString("created_at")
                                detail.add(TransactionDetails(id.toString(), amount, status, type, date))
                            }
                            detailRecieved.recieved(detail)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        if (single){
                            progressCheck.error()
                        } else{
                            detailRecieved.error()
                        }

                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    error.printStackTrace()
                    if (single){
                        progressCheck.error()
                    } else{
                        detailRecieved.error()
                    }

                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> =
                        HashMap()
                    headers["Content-type"] =
                        "application/json"
                    headers["Authorization"] = "Bearer FLWSECK-2047ac195430e50a46398fc9910921cb-X"
                    return headers
                }

            }


        mQueue!!.add(request)
    }

}
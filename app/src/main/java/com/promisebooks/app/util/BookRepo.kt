package com.promisebooks.app.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.promisebooks.app.merchant.TransactionDataFactory
import com.promisebooks.app.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookRepo {
    private lateinit var ussdRecieved: Transaction
    private lateinit var detailRecieved: DetailRecieved
    private lateinit var accountRecieved: AccountRecieved
    private lateinit var context: Context
    private lateinit var refundVerify: RefundVerify
    private lateinit var progressCheck: ProgressCheck
    private var mQueue: RequestQueue? = null
   lateinit var itemPagedList: LiveData<PagedList<TransactionDetails>>
    private lateinit var liveDataSource: LiveData<PageKeyedDataSource<Int, TransactionDetails>>


    constructor(refundVerify: RefundVerify, context: Context){
        this.refundVerify = refundVerify
        this.context = context
        mQueue = VolleyRequest.getVolley(context)
    }
    constructor(detail: DetailRecieved, context: Context, url: String){
        this.detailRecieved = detail
        mQueue = VolleyRequest.getVolley(context)
        val itemDataSourceFactory = TransactionDataFactory(context, url)
        liveDataSource = itemDataSourceFactory.getItemLiveDataSource()

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(14)
            .setPageSize(7)
            .build()

        itemPagedList = LivePagedListBuilder<Int, TransactionDetails>(itemDataSourceFactory, config).build()
    }

    constructor(progressCheck: ProgressCheck){
        this.progressCheck = progressCheck
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
              ussdRecieved.errorTransaction()
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
                ussdRecieved.errorTransaction()
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
           accountRecieved.errorAccount()
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
        val request: JsonObjectRequest = object : JsonObjectRequest(
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
                                progressCheck.errorProgress()
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
                            progressCheck.errorProgress()
                        } else{
                            detailRecieved.errorDetail()
                        }

                    }
                },
                com.android.volley.Response.ErrorListener { error: VolleyError ->
                    error.printStackTrace()
                    if (single){
                        progressCheck.errorProgress()
                    } else{
                        detailRecieved.errorDetail()
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

    fun getRefundTransaction(ref: String) {
        var detail = RefundRequest()
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "https://api.flutterwave.com/v3/transactions?tx_ref=$ref", null,
            com.android.volley.Response.Listener { response: JSONObject ->
                try {
                    val data = response.getJSONArray("data")
                    val properties = data.getJSONObject(0)
                    val id = properties.getInt("id")
                    val amount = properties.getInt("amount")
                    val processorResponse = properties.getString("status")
                    val flwRref = properties.getString("flw_ref")
                    if (processorResponse == "successful"){
                        detail = RefundRequest(amount = amount, flw_ref = flwRref)
                        refundVerify.verified(id, detail)
                    } else{
                        refundVerify.Refunderror()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    refundVerify.Refunderror()
                }
            },
            com.android.volley.Response.ErrorListener { error: VolleyError ->
                error.printStackTrace()
                refundVerify.Refunderror()
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
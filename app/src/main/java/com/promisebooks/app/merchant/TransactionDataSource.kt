package com.promisebooks.app.merchant

import android.content.Context
import androidx.paging.PageKeyedDataSource
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.promisebooks.app.model.TransactionDetails
import com.promisebooks.app.util.VolleyRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.set


class TransactionDataSource(val context: Context, val url: String): PageKeyedDataSource<Int, TransactionDetails>() {
    private val first_page = 1
    private var mQueue: RequestQueue? = null

    init {
        mQueue = VolleyRequest.getVolley(context)
    }


    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, TransactionDetails>) {
        val detail: MutableList<TransactionDetails> = ArrayList<TransactionDetails>()
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "$url&page=$first_page", null,
            com.android.volley.Response.Listener { response: JSONObject ->
                try {

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

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                callback.onResult(detail, null, first_page + 1)
            },
            com.android.volley.Response.ErrorListener { error: VolleyError ->
                error.printStackTrace()

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

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionDetails>) {
        val detail: MutableList<TransactionDetails> = ArrayList<TransactionDetails>()
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "$url&page=${params.key}", null,
            com.android.volley.Response.Listener { response: JSONObject ->
                try {

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

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val key: Int? = if (params.key > 1){
                    params.key + 1
                }else{
                    null
                }
                callback.onResult(detail, key)
            },
            com.android.volley.Response.ErrorListener { error: VolleyError ->
                error.printStackTrace()

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

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TransactionDetails>) {
        val detail: MutableList<TransactionDetails> = ArrayList<TransactionDetails>()
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "$url&page=${params.key}", null,
            com.android.volley.Response.Listener { response: JSONObject ->
                try {

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

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                val key: Int? = if (params.key > 1){
                    params.key - 1
                }else{
                    null
                }
                callback.onResult(detail, key)
            },
            com.android.volley.Response.ErrorListener { error: VolleyError ->
                error.printStackTrace()

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
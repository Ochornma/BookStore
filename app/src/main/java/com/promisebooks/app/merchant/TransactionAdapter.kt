package com.promisebooks.app.merchant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.promisebooks.app.R
import com.promisebooks.app.databinding.TransactionItemBinding
import com.promisebooks.app.model.TransactionDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionAdapter: RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    private var trans:MutableList<TransactionDetails> = ArrayList<TransactionDetails>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
      val binding = DataBindingUtil.inflate<TransactionItemBinding>(LayoutInflater.from(parent.context), R.layout.transaction_item, parent, false)
        return TransactionHolder(binding)
    }

    override fun getItemCount(): Int {
        return trans.size
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        holder.binding.trans = trans[position]
        holder.binding.dateText.text = trans[position].date.getDateWithServerTimeStamp().toString()
    }

    fun setDetail(trans: MutableList<TransactionDetails>){
        this.trans = trans
    }

    fun String.getDateWithServerTimeStamp(): Date? {
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        )
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")  // IMP !!!
        return try {
            dateFormat.parse(this)
        } catch (e: ParseException) {
            null
        }
    }
    class TransactionHolder(val binding: TransactionItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}
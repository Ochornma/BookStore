package com.promisebooks.app.customer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.promisebooks.app.R
import com.promisebooks.app.databinding.PaymentItemBinding
import com.promisebooks.app.model.Data1

class PaymentAdapter: RecyclerView.Adapter<PaymentAdapter.PaymentHolder>() {

    private var bank:MutableList<Data1> = ArrayList<Data1>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val binding = DataBindingUtil.inflate<PaymentItemBinding>(LayoutInflater.from(parent.context), R.layout.payment_item, parent, false)
        return PaymentHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return bank.size
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
      holder.binding.data = bank[position]
    }

    fun setBank(bank: MutableList<Data1>){
        this.bank = bank
    }

    class PaymentHolder(val binding: PaymentItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

}


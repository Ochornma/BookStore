package com.promisebooks.app.merchant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.promisebooks.app.R
import com.promisebooks.app.databinding.RefundItemBinding
import com.promisebooks.app.model.Refund
import com.squareup.picasso.Picasso

class RefundAdapter(option: FirestorePagingOptions<Refund>, private val clickedRefund: ClickedRefund, private val swipeRefreshLayout: SwipeRefreshLayout, val context: Context): FirestorePagingAdapter<Refund, RefundAdapter.RefundHolder>(option) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefundHolder {
       val binding = DataBindingUtil.inflate<RefundItemBinding>(LayoutInflater.from(parent.context), R.layout.refund_item, parent, false)
        return RefundHolder(binding)
    }

    override fun onBindViewHolder(holder: RefundHolder, position: Int, model: Refund) {
        holder.binding.message = model
        holder.binding.confirmButton.setOnClickListener {
            clickedRefund.click(it, model)
        }
        Picasso.get().load(model.image).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).fit().into(holder.binding.image)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL -> {
                swipeRefreshLayout.isRefreshing = true
            }

            LoadingState.LOADING_MORE -> {
                swipeRefreshLayout.isRefreshing = true
            }

            LoadingState.LOADED -> {
                swipeRefreshLayout.isRefreshing = false
            }

            LoadingState.ERROR -> {
                Toast.makeText(context, "Error Occurred!", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }

            LoadingState.FINISHED -> {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    class RefundHolder(val binding: RefundItemBinding): RecyclerView.ViewHolder(binding.root)
}

interface ClickedRefund{
    fun click(view: View, refund: Refund)
}
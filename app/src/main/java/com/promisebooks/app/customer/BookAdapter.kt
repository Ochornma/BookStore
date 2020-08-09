package com.promisebooks.app.customer

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
import com.promisebooks.app.databinding.MarketItemBinding
import com.promisebooks.app.model.Book
import com.squareup.picasso.Picasso

class BookAdapter(option: FirestorePagingOptions<Book>, val clicked: Clicked, val swipeRefreshLayout: SwipeRefreshLayout, val context: Context): FirestorePagingAdapter<Book, BookAdapter.BookHolder>(option) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val binding = DataBindingUtil.inflate<MarketItemBinding>(LayoutInflater.from(parent.context), R.layout.market_item, parent, false)
        return BookHolder(binding)
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int, model: Book) {
        holder.binding.message = model
        holder.binding.watch.text = "NGN${model.price}"
        holder.binding.root.setOnClickListener {
            clicked.click(it, model)
        }
        holder.binding.watch.setOnClickListener {
            clicked.click(it, model)
        }
        holder.binding.listen.setOnClickListener {
            clicked.click(it, model)
        }
        holder.binding.cart.setOnClickListener {
            clicked.cart(model)
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


    class BookHolder(val binding: MarketItemBinding): RecyclerView.ViewHolder(binding.root) {

    }


}
interface Clicked{
    fun click(view:View, book: Book)
    fun cart(book: Book)
}



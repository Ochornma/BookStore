package com.promisebooks.app.customer

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.promisebooks.app.R
import com.promisebooks.app.databinding.MarketItemBinding
import com.promisebooks.app.model.Book
import com.squareup.picasso.Picasso

class BookAdapter(option: FirestoreRecyclerOptions<Book>): FirestoreRecyclerAdapter<Book, BookAdapter.BookHolder>(option) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val binding = DataBindingUtil.inflate<MarketItemBinding>(LayoutInflater.from(parent.context), R.layout.market_item, parent, false)
        return BookHolder(binding)
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int, model: Book) {
        holder.binding.message = model
        holder.binding.watch.text = "NGN${model.price}"
        Picasso.get().load(model.image).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).fit().into(holder.binding.image)
    }

    class BookHolder(val binding: MarketItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}
package com.promisebooks.app.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.promisebooks.app.R
import com.promisebooks.app.databinding.BookItemBinding
import com.promisebooks.app.model.BookBought
import com.squareup.picasso.Picasso

class MyBooksAdapater: RecyclerView.Adapter<MyBooksAdapater.MyBookHolder>() {

 private var books:MutableList<BookBought> = ArrayList<BookBought>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookHolder {
        val binding = DataBindingUtil.inflate<BookItemBinding>(LayoutInflater.from(parent.context), R.layout.book_item, parent, false)
        return MyBookHolder(binding)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: MyBookHolder, position: Int) {
     holder.binding.message = books[position]
        Picasso.get().load(books[position].image).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).fit().into(holder.binding.image)
    }

    fun setBook(book: MutableList<BookBought>){
        this.books = book
    }

    class MyBookHolder(val binding: BookItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}
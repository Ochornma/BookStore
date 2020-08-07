package com.promisebooks.app.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.promisebooks.app.R
import com.promisebooks.app.databinding.MarketItemBinding
import com.promisebooks.app.model.Book
import com.squareup.picasso.Picasso

class BookAdapter(option: FirestorePagingOptions<Book>, val clicked: Clicked): FirestorePagingAdapter<Book, BookAdapter.BookHolder>(option) {



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
        Picasso.get().load(model.image).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).fit().into(holder.binding.image)
    }

    class BookHolder(val binding: MarketItemBinding): RecyclerView.ViewHolder(binding.root) {

    }


}
interface Clicked{
    fun click(view:View, book: Book)
}



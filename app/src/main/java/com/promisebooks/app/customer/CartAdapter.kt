package com.promisebooks.app.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.promisebooks.app.R
import com.promisebooks.app.databinding.CartItemBinding
import com.promisebooks.app.model.Cart
import com.squareup.picasso.Picasso

class CartAdapter(val clicked: Clicked): RecyclerView.Adapter<CartAdapter.CartHolder>() {
    private var carts:MutableList<Cart> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder{
        val binding = DataBindingUtil.inflate<CartItemBinding>(
            LayoutInflater.from(parent.context), R.layout.cart_item, parent, false)
        return CartHolder(binding)
    }

    override fun getItemCount() = carts.size

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val cart = carts[position]
        holder.binding.message = cart
        holder.binding.price.text = "NGN ${cart.price}"
        holder.binding.quantity.text = "QTY: ${cart.qty}"
        Picasso.get().load(cart.image).placeholder(R.drawable.app_icon).error(R.drawable.app_icon).fit().into(holder.binding.image)
        holder.binding.remove.setOnClickListener {
            clicked.remove(cart)
        }
        holder.binding.checkOut.setOnClickListener {
            clicked.pay(cart, it)
        }

        holder.binding.refund.setOnClickListener {
            clicked.refund(cart)
        }
    }

    fun setCart(carts: MutableList<Cart>){
        this.carts = carts
    }

    class CartHolder(val binding: CartItemBinding): RecyclerView.ViewHolder(binding.root)

    interface Clicked{
        fun remove(cart: Cart)
        fun refund(cart: Cart)
        fun pay(cart: Cart, view: View)
    }
}
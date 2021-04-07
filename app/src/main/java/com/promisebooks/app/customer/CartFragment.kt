package com.promisebooks.app.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.customer.adapter.CartAdapter
import com.promisebooks.app.databinding.CartFragmentBinding
import com.promisebooks.app.model.Cart
import com.promisebooks.app.model.Refund
import com.promisebooks.app.util.BaseFragment
import com.promisebooks.app.util.CartDeleteCalback
import com.promisebooks.app.util.CartRefundCallback

class CartFragment : BaseFragment<CartFragmentBinding, CartViewModel>(), CartAdapter.Clicked, CartDeleteCalback, CartRefundCallback {


    private lateinit var drawer: DrawerLayout

    private lateinit var adapater: CartAdapter

    companion object {
        fun newInstance() = CartFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.cart_fragment, container, false)
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
        binding.swipeRefresh.isRefreshing = true
        adapater = CartAdapter(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapater
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }
    }



    private fun getData(){
        val carts: MutableList<Cart> = user?.let { viewModel.getCart(it.uid) }!!
        if (carts.isNotEmpty()){
            adapater.setCart(carts)
            binding.recyclerView.adapter = adapater
            adapater.notifyDataSetChanged()
        }else{
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
        }

        binding.swipeRefresh.isRefreshing = false
    }


    override fun remove(cart: Cart) {
        binding.swipeRefresh.isRefreshing = true
        user?.email?.let { viewModel.deleteCart(cart, it) }

    }

    override fun refund(cart: Cart) {
        binding.swipeRefresh.isRefreshing = true
        val refund = Refund(cart.image, cart.title, cart.name, cart.ref)
        user?.email?.let { viewModel.refundcart(refund, cart, it) }
    }

    override fun pay(cart: Cart, view: View) {
        val action = CartFragmentDirections.actionCartFragmentToCartPaymentFragment(cart)
        Navigation.findNavController(view).navigate(action)
    }

    override fun deleteCallback() {
        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
        getData()
    }

    override fun refundCallback() {
        Toast.makeText(context, "Refund Requested", Toast.LENGTH_SHORT).show()
        getData()
    }


    override fun getViewModel(): Class<CartViewModel> {
        return CartViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CartFragmentBinding {
      return CartFragmentBinding.inflate(inflater, container, false)
    }

}
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.CartFragmentBinding
import com.promisebooks.app.model.Cart
import com.promisebooks.app.model.Refund
import java.util.*

class CartFragment : Fragment(), CartAdapter.Clicked {
    private lateinit var binding: CartFragmentBinding
    private var db = FirebaseFirestore.getInstance()
    private var collectionCart = db.collection("Cart")
    private var collectionRefund = db.collection("Refund")
    private var user = FirebaseAuth.getInstance().currentUser
    private var uiid = " "
    private var email = " "
    private lateinit var drawer: DrawerLayout
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private lateinit var adapater:CartAdapter

    companion object {
        fun newInstance() = CartFragment()
    }

    private lateinit var viewModel: CartViewModel

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

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener { it ->
            if (it.currentUser != null){
                /*   if (merchant(it.currentUser!!.email!!)){
                       val intent = Intent(activity, MerchantActivity::class.java)
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                       startActivity(intent)
                       activity?.finish()
                   }*/
                uiid = user?.uid!!
                email = user?.email!!
                getData()
            }else{
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                activity?.let {it1 ->
                    it1.startActivity(
                        Intent(it1, AuthActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    it1.finish()}


            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    private fun getData(){
        val carts: MutableList<Cart> = ArrayList()
        collectionCart.whereEqualTo("id", uiid).get().addOnSuccessListener {
            if (!it.isEmpty){

                val list = it.documents
                for (item in list){
                    val cart = item.toObject(Cart::class.java)
                    if (cart != null) {
                        carts.add(cart)
                    }
                }
                adapater.setCart(carts)
                binding.recyclerView.adapter = adapater
                adapater.notifyDataSetChanged()
                binding.swipeRefresh.isRefreshing = false
            } else{
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()

            }
        }

    }

    override fun onStart() {
        super.onStart()
        setUpListener()
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }





    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }

    override fun remove(cart: Cart) {
        binding.swipeRefresh.isRefreshing = true
       collectionCart.document("${cart.title}_$email").delete().addOnSuccessListener {
           Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
           getData()
       }
    }

    override fun refund(cart: Cart) {
        binding.swipeRefresh.isRefreshing = true
        val refund = Refund(cart.image, cart.title, cart.name, cart.ref)
        collectionRefund.document().set(refund).addOnSuccessListener {
            collectionCart.document("${cart.title}_$email").delete().addOnSuccessListener {
                Toast.makeText(context, "Refund Requested", Toast.LENGTH_SHORT).show()
                getData()
            }
        }
    }

    override fun pay(cart: Cart, view: View) {
        val action = CartFragmentDirections.actionCartFragmentToCartPaymentFragment(cart)
        Navigation.findNavController(view).navigate(action)
    }

}
package com.promisebooks.app.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.promisebooks.app.R
import com.promisebooks.app.customer.adapter.BookAdapter
import com.promisebooks.app.customer.adapter.Clicked
import com.promisebooks.app.databinding.MarketFragmentBinding
import com.promisebooks.app.model.Book
import com.promisebooks.app.util.BaseFragment
import com.promisebooks.app.util.CartCallback


class MarketFragment : BaseFragment<MarketFragmentBinding, MarketViewModel>(), Clicked, CartCallback {

    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Books")
    private lateinit var drawer: DrawerLayout
    private lateinit var book1: Book
    private lateinit var madapter: BookAdapter

    companion object {
        fun newInstance() = MarketFragment()
    }

    override fun setUpViews() {
        super.setUpViews()

        drawer = activity?.findViewById(R.id.drawer_layout)!!
        //setUpData()
        //getData()
        binding.swipeRefresh.setOnRefreshListener {
            madapter.refresh()
        }

        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    private fun setUpData() {
        val query = collection.orderBy("price", Query.Direction.ASCENDING)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        val options = FirestorePagingOptions.Builder<Book>()
            .setLifecycleOwner(this)
            .setQuery(query, config, Book::class.java)
            .build()

        madapter = context?.let {
            BookAdapter(
                options,
                this,
                binding.swipeRefresh,
                it
            )
        }!!
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = madapter
        madapter.startListening()
        madapter.notifyDataSetChanged()

    }





    override fun onStart() {
        super.onStart()
        setUpData()
        madapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        madapter.stopListening()
    }


/*    private fun merchant(email: String): Boolean{
        return (email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
    }*/

    override fun click(view: View, book: Book) {
        book1 = book
        val action = MarketFragmentDirections.actionMarketFragmentToPaymentFragment(book)
        Navigation.findNavController(view).navigate(action)
    }

    override fun cart(book: Book) {
        binding.swipeRefresh.isRefreshing = true

        val email = user?.email
        val uiid = user?.uid
        val ref = email + "_" + viewModel.getTime()
        viewModel.cartCollection(book, email!!, ref, uiid!!, customUser.name, customUser.phone, this )
    }

    override fun getViewModel(): Class<MarketViewModel> {
        return MarketViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MarketFragmentBinding {
        return MarketFragmentBinding.inflate(inflater, container, false)
    }

    override fun callback() {
        Toast.makeText(context, "Book added to cart", Toast.LENGTH_SHORT).show()
        binding.swipeRefresh.isRefreshing = false
    }

    override fun callbackError() {

    }


}
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
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.customer.adapter.BookAdapter
import com.promisebooks.app.customer.adapter.Clicked
import com.promisebooks.app.databinding.MarketFragmentBinding
import com.promisebooks.app.model.Book
import com.promisebooks.app.model.Cart
import com.promisebooks.app.model.User
import java.text.SimpleDateFormat
import java.util.*


class MarketFragment : Fragment(),
    Clicked {
    private lateinit var binding: MarketFragmentBinding
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Books")
    private var collectionUser = db.collection("Users")
    private var collectionCart = db.collection("Cart")
    private lateinit var drawer: DrawerLayout
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private lateinit var book1: Book
    private var uiid = " "
    private var name = ""
    private var phone = ""
    private var email: String = " "
    private lateinit var madapter: BookAdapter

    companion object {
        fun newInstance() = MarketFragment()
    }

    private lateinit var viewModel: MarketViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.market_fragment, container, false)
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        //setUpData()
        //getData()
        binding.swipeRefresh.setOnRefreshListener {
            madapter.refresh()
        }

        binding.menu.setOnClickListener {
         drawer.openDrawer(GravityCompat.START)
        }
        return binding.root
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

/*    fun getData(){
  *//*      val books: MutableList<Book> = ArrayList<Book>()
        collection.get().addOnSuccessListener {
            if (!it.isEmpty){
                val list = it.documents
                for (item in list){
                    val book = item.toObject(Book::class.java)
                    if (book != null) {
                        books.add(book)
                    }
                }
                adapter.notifyDataSetChanged()
                binding.progress.visibility = View.GONE
            }
        }*//*
    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MarketViewModel::class.java)
        setUpData()
       // getData()
    }

    override fun onStart() {
        super.onStart()
        setUpData()
        setUpListener()
        madapter.startListening()
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    override fun onStop() {
        super.onStop()
        madapter.stopListening()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null){
          /*      if (merchant(it.currentUser!!.email!!)){
                    val intent = Intent(activity, MerchantActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    activity?.finish()
                }*/
                email = it.currentUser?.email!!
                uiid = it.currentUser?.uid!!
                collectionUser.document(uiid).get().addOnSuccessListener { it1 ->
                    val user = it1.toObject<User>(User::class.java)
                    if (user != null) {
                        phone = user.phone
                        name = user.name
                    }
                }

            }else{
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
               /* val intent = Intent(activity?.applicationContext, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity?.startActivity(intent)
                activity?.finish()*/
                activity?.let {it1 ->
                    it1.startActivity(Intent(it1, AuthActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    it1.finish()}
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
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
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH)
        val strDate: String = sdf.format(c.time)
        val ref = email + "_" + strDate
        collectionCart.document("${book.title}_$email").get().addOnSuccessListener {
            if (it.exists()){
                val cart1 = it.toObject(Cart::class.java)
                val qty = cart1?.qty
                val cart = Cart(book.title, book.image, book.description, book.price, false, ref, uiid, name, phone,
                    qty?.plus(1)!!
                )
                collectionCart.document("${book.title}_$email").set(cart).addOnSuccessListener {
                    Toast.makeText(context, "Book added to cart", Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            } else{
                val cart = Cart(book.title, book.image, book.description, book.price, false, ref, uiid, name, phone, 1)
                collectionCart.document("${book.title}_$email").set(cart).addOnSuccessListener {
                    Toast.makeText(context, "Book added to cart", Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }


    }


}
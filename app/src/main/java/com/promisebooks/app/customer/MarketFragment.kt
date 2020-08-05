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
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.MarketFragmentBinding
import com.promisebooks.app.merchant.MerchantActivity
import com.promisebooks.app.model.Book
import java.util.*
import kotlin.collections.ArrayList

class MarketFragment : Fragment() {
    private lateinit var binding: MarketFragmentBinding
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Books")
    private lateinit var drawer: DrawerLayout
    private lateinit var authListner: FirebaseAuth.AuthStateListener

    private lateinit var adapter: BookAdapter

    companion object {
        fun newInstance() = MarketFragment()
    }

    private lateinit var viewModel: MarketViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.market_fragment, container, false)
        setUpListener()
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        setUpData()
        getData()
        binding.menu.setOnClickListener {
         drawer.openDrawer(GravityCompat.START)
        }
        return binding.root
    }

    private fun setUpData() {
        val query = collection.orderBy("price", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Book>()
            .setQuery(query, Book::class.java)
            .build()
        adapter = BookAdapter(options)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

    }

    fun getData(){
        val books: MutableList<Book> = ArrayList<Book>()
        collection.get().addOnSuccessListener {
            if (!it.isEmpty){
                val list = it.documents
                for (item in list){
                    val book = item.toObject(Book::class.java)
                    if (book != null) {
                        books.add(book)
                    }
                }
               // adapter.setBook(books)
                adapter.notifyDataSetChanged()
            }
            Toast.makeText(context, "${books.size}", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MarketViewModel::class.java)
        setUpData()
       // getData()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null){
                if (merchant(it.currentUser!!.email!!)){
                    val intent = Intent(activity, MerchantActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    activity?.finish()
                }

            }else{
                val intent = Intent(activity, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                activity?.finish()
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    private fun merchant(email: String): Boolean{
        return (email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
    }
}
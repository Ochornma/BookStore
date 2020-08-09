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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.MyBookFragmentBinding
import com.promisebooks.app.model.BookBought
import java.util.*


class MyBookFragment : Fragment() {
    private lateinit var binding:MyBookFragmentBinding
    private lateinit var adapater: MyBooksAdapater
    private var db = FirebaseFirestore.getInstance()

    private var collectionProduct = db.collection("BoughtProducts")
    private var user = FirebaseAuth.getInstance().currentUser
    private var uiid = " "
    private lateinit var drawer: DrawerLayout
    private lateinit var authListner: FirebaseAuth.AuthStateListener

    companion object {
        fun newInstance() = MyBookFragment()
    }

    private lateinit var viewModel: MyBookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_book_fragment, container, false)
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }


        binding.swipeRefresh.isRefreshing = true
        adapater = MyBooksAdapater()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapater
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyBookViewModel::class.java)
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
                getData()
            }else{
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
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

    private fun getData(){
        val books: MutableList<BookBought> = ArrayList()
        collectionProduct.whereEqualTo("id", uiid).get().addOnSuccessListener {
            if (!it.isEmpty){

                val list = it.documents
                for (item in list){
                    val book = item.toObject(BookBought::class.java)
                    if (book != null) {
                        books.add(book)
                    }
                }
                adapater.setBook(books)
                binding.recyclerView.adapter = adapater
                adapater.notifyDataSetChanged()
                binding.swipeRefresh.isRefreshing = false
            } else{
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()

            }
        }

    }

/*    private fun setUpData() {
        val query = collectionProduct.orderBy("price")
            .startAt(uiid)
            .endAt(uiid)*//*.whereEqualTo("id", uiid)*//*
          *//*.orderBy("price", Query.Direction.ASCENDING).whereEqualTo("id", uiid)*//*
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        val options = FirestorePagingOptions.Builder<BookBought>()
            .setLifecycleOwner(this)
            .setQuery(query, config, BookBought::class.java)
            .build()


        adapater = context?.let { MyBooksAdapater(options , binding.swipeRefresh, it) }!!
        adapater.updateOptions(options)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapater
        adapater.startListening()
        adapater.notifyDataSetChanged()

    }*/
    override fun onStart() {
        super.onStart()
        setUpListener()
       FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }





    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }

}


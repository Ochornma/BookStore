package com.promisebooks.app.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.MyBookFragmentBinding
import com.promisebooks.app.merchant.MerchantActivity
import com.promisebooks.app.model.Book
import com.promisebooks.app.model.BookBought
import java.util.*


class MyBookFragment : Fragment() {
    private lateinit var binding:MyBookFragmentBinding
    private lateinit var adapater: MyBooksAdapater
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Users")
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

        adapater = MyBooksAdapater()
        binding.progreess.visibility =View.VISIBLE
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapater
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyBookViewModel::class.java)

    }

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
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
                val intent = Intent(activity?.applicationContext, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity?.startActivity(intent)
                activity?.finish()
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    private fun merchant(email: String): Boolean{
        return (email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
    }

    fun getData(){

        val books: MutableList<BookBought> = ArrayList<BookBought>()
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
                binding.progreess.visibility = View.GONE
            }
        }
    }
    override fun onStart() {
        super.onStart()
        setUpListener()
       FirebaseAuth.getInstance().addAuthStateListener(authListner)
        getData()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }

}


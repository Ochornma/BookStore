package com.promisebooks.app.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.promisebooks.app.R
import com.promisebooks.app.databinding.MyBookFragmentBinding
import com.promisebooks.app.model.BookBought
import com.promisebooks.app.util.BaseFragment
import com.promisebooks.app.util.CartBookCallback


class MyBookFragment : BaseFragment<MyBookFragmentBinding, MyBookViewModel>(), CartBookCallback {

    private lateinit var adapater: MyBooksAdapater
    private lateinit var drawer: DrawerLayout


    companion object {
        fun newInstance() = MyBookFragment()
    }



    override fun setUpViews() {
        super.setUpViews()
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
        binding.swipeRefresh.isRefreshing = true
        adapater = MyBooksAdapater()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapater
        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData(){
    viewModel.getBook(user!!.uid, this)

    }


    override fun getViewModel(): Class<MyBookViewModel> = MyBookViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MyBookFragmentBinding = MyBookFragmentBinding.inflate(inflater, container, false)

    override fun callback(book: MutableList<BookBought>) {
        adapater.setBook(book)
        binding.recyclerView.adapter = adapater
        adapater.notifyDataSetChanged()
        binding.swipeRefresh.isRefreshing = false
    }

    override fun callbackError() {
        binding.swipeRefresh.isRefreshing = false
        Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
    }
}


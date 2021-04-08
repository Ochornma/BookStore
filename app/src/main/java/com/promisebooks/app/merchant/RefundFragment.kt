package com.promisebooks.app.merchant

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.RefundFragmentBinding
import com.promisebooks.app.model.Refund
import com.promisebooks.app.model.RefundRequest
import com.promisebooks.app.util.BaseFragment2
import com.promisebooks.app.util.K
import com.promisebooks.app.util.RefundVerify

class RefundFragment : BaseFragment2<RefundFragmentBinding>(), ClickedRefund, RefundVerify {

    private lateinit var drawer: DrawerLayout

    private lateinit var madapter: RefundAdapter
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Refund")

    companion object {
        fun newInstance() = RefundFragment()
    }

    private lateinit var viewModel: RefundViewModel



    override fun setUpViewModel() {
        super.setUpViewModel()
        val factory = context?.let { RefundFactory(this, it) }!!
        viewModel = ViewModelProvider(this, factory).get(RefundViewModel::class.java)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpData()
        // getData()
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

    private fun setUpData() {
        val query = collection.orderBy("customer", Query.Direction.ASCENDING)
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        val options = FirestorePagingOptions.Builder<Refund>()
            .setLifecycleOwner(this)
            .setQuery(query, config, Refund::class.java)
            .build()

        madapter = context?.let {
            RefundAdapter(
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



    override fun click(view: View, refund: Refund) {
        binding.swipeRefresh.isRefreshing = true
        viewModel.getTransact(refund.ref)
    }

    override fun verified(id: Int, refundRequest: RefundRequest) {
        binding.swipeRefresh.isRefreshing = false
        val builder: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
        builder?.setNegativeButton(activity?.resources?.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            // Since we cannot proceed, we should finish the activity
        }
        builder?.setPositiveButton("Approve Refund"){
                dialog, _ ->
            dialog.dismiss()
        }
        builder?.setMessage("Transaction was Found")
        builder?.create()?.show()
    }

    override fun Refunderror() {
        activity?.let { K.alert("Transaction not found", null, it, false) }
        binding.swipeRefresh.isRefreshing = false
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RefundFragmentBinding = RefundFragmentBinding.inflate(inflater, container, false)

}
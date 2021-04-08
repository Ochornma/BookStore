package com.promisebooks.app.merchant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.promisebooks.app.databinding.TransactionViewFragmentBinding
import com.promisebooks.app.model.TransactionDetails
import com.promisebooks.app.util.BaseFragment2
import com.promisebooks.app.util.DetailRecieved
import com.promisebooks.app.util.BookView

class TransactionViewFragment : BaseFragment2<TransactionViewFragmentBinding>(), DetailRecieved {

    private var url = ""
    private lateinit var adapter: TransactionAdapter
    companion object {
        fun newInstance() = TransactionViewFragment()
    }
    private lateinit var viewModel: TransactionViewViewModel


    override fun setUpViewModel() {
        super.setUpViewModel()
        val factory = context?.let { TransactionFactory(this, it, url) }!!
        viewModel = ViewModelProvider(this, factory).get(TransactionViewViewModel::class.java)
        viewModel.trans().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.progress.visibility = View.GONE
        })
        binding.recyclerView.adapter = adapter
    }

    override fun setUpViews() {
        super.setUpViews()
        binding.progress.visibility = View.VISIBLE
        val args: TransactionViewFragmentArgs by navArgs()
        url = args.ref
        adapter = TransactionAdapter()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
    }
    override fun recieved(detail: MutableList<TransactionDetails>) {
        binding.progress.visibility = View.GONE
    }

    override fun errorDetail() {
        activity?.let { BookView.alert("error in retrieving transactions", binding.progress, it , true) }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): TransactionViewFragmentBinding = TransactionViewFragmentBinding.inflate(inflater, container, false)


}
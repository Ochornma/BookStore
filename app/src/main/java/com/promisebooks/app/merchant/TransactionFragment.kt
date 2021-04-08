package com.promisebooks.app.merchant

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.TransactionFragmentBinding
import com.promisebooks.app.util.BaseFragment

class TransactionFragment : BaseFragment<TransactionFragmentBinding, TransactionViewModel>() {

    private lateinit var drawer: DrawerLayout
    private lateinit var authListner: FirebaseAuth.AuthStateListener

    companion object {
        fun newInstance() = TransactionFragment()
    }


    override fun setUpViews() {
        super.setUpViews()
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.all.setOnClickListener {
            val year = binding.yearInput.text.toString()
            val year1 = binding.year1Input.text.toString()
            val month = binding.monthInput.text.toString()
            val month1 = binding.month1Input.text.toString()
            val day = binding.dateInput.text.toString()
            val day1 = binding.date1Input.text.toString()
            val url = "https://api.flutterwave.com/v3/transactions?from=$year-${month}-$day&to=$year1-${month1}-$day1"
            val actions = TransactionFragmentDirections.actionTransactionFragmentToTransactionViewFragment(url)
            Navigation.findNavController(it).navigate(actions)

        }
        binding.success.setOnClickListener {
            val year = binding.yearInput.text.toString()
            val year1 = binding.year1Input.text.toString()
            val month = binding.monthInput.text.toString()
            val month1 = binding.month1Input.text.toString()
            val day = binding.dateInput.text.toString()
            val day1 = binding.date1Input.text.toString()
            val url = "https://api.flutterwave.com/v3/transactions?from=$year-${month}-$day&to=$year1-${month1}-$day1&status=successful"
            val actions = TransactionFragmentDirections.actionTransactionFragmentToTransactionViewFragment(url)
            Navigation.findNavController(it).navigate(actions)
        }

        binding.fail.setOnClickListener {
            val year = binding.yearInput.text.toString()
            val year1 = binding.year1Input.text.toString()
            val month = binding.monthInput.text.toString()
            val month1 = binding.month1Input.text.toString()
            val day = binding.dateInput.text.toString()
            val day1 = binding.date1Input.text.toString()
           val url = "https://api.flutterwave.com/v3/transactions?from=$year-${month}-$day&to=$year1-${month1}-$day1&status=failed"
            val actions = TransactionFragmentDirections.actionTransactionFragmentToTransactionViewFragment(url)
            Navigation.findNavController(it).navigate(actions)
        }
    }

    override fun getViewModel(): Class<TransactionViewModel> {
       return TransactionViewModel::class.java
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): TransactionFragmentBinding {
       return TransactionFragmentBinding.inflate(inflater, container, false)
    }


}
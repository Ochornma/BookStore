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

class TransactionFragment : Fragment() {
    private lateinit var binding: TransactionFragmentBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var authListner: FirebaseAuth.AuthStateListener

    companion object {
        fun newInstance() = TransactionFragment()
    }

    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_fragment, container, false)
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
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

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null){
                /*   if (merchant(it.currentUser!!.email!!)){
                       val intent = Intent(activity, MerchantActivity::class.java)
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                       startActivity(intent)
                       activity?.finish()
                   }*/

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
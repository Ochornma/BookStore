package com.promisebooks.app.merchant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.TransactionViewFragmentBinding
import com.promisebooks.app.model.TransactionDetails
import com.promisebooks.app.util.DetailRecieved
import com.promisebooks.app.util.K

class TransactionViewFragment : Fragment(), DetailRecieved {
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private var url = ""
    private lateinit var binding: TransactionViewFragmentBinding
    private lateinit var adapter: TransactionAdapter


    companion object {
        fun newInstance() = TransactionViewFragment()
    }

    private lateinit var viewModel: TransactionViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_view_fragment, container, false)
        binding.progress.visibility = View.VISIBLE
        val args: TransactionViewFragmentArgs by navArgs()
        url = args.ref
        adapter = TransactionAdapter()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =  LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = context?.let { TransactionFactory(this, it, url) }!!
        viewModel = ViewModelProvider(this, factory).get(TransactionViewViewModel::class.java)
       // viewModel.transact(url)
        viewModel.trans().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.progress.visibility = View.GONE
        })
        binding.recyclerView.adapter = adapter
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
                /*val intent = Intent(activity?.applicationContext, AuthActivity::class.java)
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



    override fun onStart() {
        super.onStart()
        setUpListener()
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }
    override fun recieved(detail: MutableList<TransactionDetails>) {
        binding.progress.visibility = View.GONE
      //adapter.setDetail(detail)
        //binding.recyclerView.adapter = adapter
       // adapter.notifyDataSetChanged()
    }

    override fun errorDetail() {
        activity?.let { K.alert("error in retrieving transactions", binding.progress, it , true) }
    }

   /* override fun error() {
       Toast.makeText(context, " Error", Toast.LENGTH_SHORT).show()
    }*/

}
package com.promisebooks.app.util

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.promisebooks.app.auth.AuthActivity

abstract class BaseFragment<B : ViewBinding, VM : ViewModel> : Fragment() {

    open var user = FirebaseAuth.getInstance().currentUser
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    protected lateinit var viewModel: VM
    protected lateinit var binding: B


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getFragmentBinding(inflater, container)
        viewModel = ViewModelProvider(this).get(getViewModel())
        return binding.root
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

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener { it ->
            if (it.currentUser == null){
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                activity?.let {it1 ->
                    it1.startActivity(
                        Intent(it1, AuthActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    it1.finish()}


            }else{
                user = it.currentUser
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B
}
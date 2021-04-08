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
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.model.User

abstract class BaseFragment<B : ViewBinding, VM : ViewModel> : Fragment() {

    var user = FirebaseAuth.getInstance().currentUser
    lateinit var customUser: User
    private var db = FirebaseFirestore.getInstance()
    private var collectionUser = db.collection("Users")
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    protected lateinit var viewModel: VM
    protected lateinit var binding: B
    open fun setUpViews() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getFragmentBinding(inflater, container)
        viewModel = ViewModelProvider(this).get(getViewModel())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
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
                user?.uid?.let { it1 ->
                    collectionUser.document(it1).get().addOnSuccessListener { it2 ->
                        val user = it2.toObject<User>(User::class.java)
                        if (user != null){
                            customUser = user
                        }


                    }
                }
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B


}
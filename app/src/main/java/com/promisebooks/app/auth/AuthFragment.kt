package com.promisebooks.app.auth


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.promisebooks.app.R
import com.promisebooks.app.customer.CustomerActivity
import com.promisebooks.app.databinding.AuthFragmentBinding
import com.promisebooks.app.merchant.MerchantActivity
import com.promisebooks.app.util.K.Companion.options
import java.util.*

class AuthFragment : Fragment() {
    private lateinit var binding: AuthFragmentBinding
    private lateinit var authListner: FirebaseAuth.AuthStateListener
 private val activity1 by lazy {
     FragmentActivity()
 }

    companion object {
        fun newInstance() = AuthFragment()
    }

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.auth_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.login.setOnClickListener {
            findNavController().navigate(R.id.loginFragment, null, options)
        }

        binding.signup.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment, null, options)
        }
    }

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null){

                if (merchant(it.currentUser!!.email!!)){
                    FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                    /*val intent = Intent(activity?.application, MerchantActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    activity1.startActivity(intent)*/
                    activity?.let {it1 ->
                        it1.startActivity(Intent(it1, MerchantActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        it1.finish()
                        }

                } else{
                    FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                   /* val intent = Intent(activity?.application, CustomerActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    activity1.startActivity(intent)*/
                    activity?.let {it1 ->
                        it1.startActivity(Intent(it1, CustomerActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        it1.finish()
                    }
                }
                //activity1.finish()
            }
        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    private fun merchant(email: String): Boolean{
        return (email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
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
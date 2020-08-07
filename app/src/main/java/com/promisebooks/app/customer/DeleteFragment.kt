package com.promisebooks.app.customer

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.DeleteFragmentBinding
import com.promisebooks.app.merchant.MerchantActivity
import java.util.*

class DeleteFragment : Fragment() {

    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private lateinit var binding:DeleteFragmentBinding
    private lateinit var drawer: DrawerLayout
    private var email = " "


    companion object {
        fun newInstance() = DeleteFragment()
    }

    private lateinit var viewModel: DeleteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.delete_fragment, container, false)
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
        setUpListener()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DeleteViewModel::class.java)


        binding.delete.setOnClickListener {
            val credential = EmailAuthProvider
                .getCredential(email, binding.passwordInput.text.toString())
            FirebaseAuth.getInstance().currentUser?.reauthenticate(credential)?.addOnSuccessListener {
                Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun setUpListener(){
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null){
            /*    if (merchant(it.currentUser!!.email!!)){
                    val intent = Intent(activity, MerchantActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    activity?.finish()
                }*/
                email = it.currentUser?.email!!
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

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListner)
    }
}
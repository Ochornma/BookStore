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
import com.promisebooks.app.util.BaseFragment
import java.util.*

class DeleteFragment : BaseFragment<DeleteFragmentBinding, DeleteViewModel>() {

    private lateinit var drawer: DrawerLayout

    companion object {
        fun newInstance() = DeleteFragment()
    }


    override fun setUpViews() {
        super.setUpViews()
        drawer = activity?.findViewById(R.id.drawer_layout)!!
        binding.menu.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }
        binding.delete.setOnClickListener {
            if (binding.passwordInput.text.toString().isNotEmpty() and user!!.email!!.trim().isNotEmpty()){
                val credential = EmailAuthProvider
                    .getCredential(user!!.email!!, binding.passwordInput.text.toString())
                FirebaseAuth.getInstance().currentUser?.reauthenticate(credential)?.addOnSuccessListener {
                    FirebaseAuth.getInstance().currentUser?.delete()?.addOnSuccessListener {
                        Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                        activity?.let {it1 ->
                            it1.startActivity(Intent(it1, AuthActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            it1.finish()}
                    }

                }
            }else{
                Toast.makeText(context, "Enter your PASSWORD", Toast.LENGTH_SHORT).show()
            }

        }

        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.let {it1 ->
                it1.startActivity(Intent(it1, AuthActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                it1.finish()}
        }
    }


    private fun merchant(email: String): Boolean{
        return (user!!.email!!.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
    }



    override fun getViewModel(): Class<DeleteViewModel> = DeleteViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DeleteFragmentBinding = DeleteFragmentBinding.inflate(inflater, container, false)
}
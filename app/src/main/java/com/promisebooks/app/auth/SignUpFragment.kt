package com.promisebooks.app.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.promisebooks.app.R
import com.promisebooks.app.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: SignUpFragmentBinding

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.backPress.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.submit.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            if (email.isNotEmpty() and password.isNotEmpty()){
                binding.progressCircular.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
                signUp(email = email, password = password)
            } else{
                Toast.makeText(context, "Email and Password can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(email: String, password: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                binding.progressCircular.visibility = View.GONE
                activity?.onBackPressed()
            } else{
                binding.progressCircular.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
            }
        }
    }

}
package com.promisebooks.app.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.promisebooks.app.R
import com.promisebooks.app.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.backPress.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.submit.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() and password.isNotEmpty()){
                binding.progressCircular.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
                login(email = email, password = password)
            } else{
                Toast.makeText(context, "Email and Password can't be empty", Toast.LENGTH_SHORT).show()
            }

        }

        binding.forgetPassword.setOnClickListener {
            val email = binding.emailInput.text.toString()

            if (email.isEmpty()){
                Toast.makeText(context, "Enter your Email Address", Toast.LENGTH_SHORT).show()
            } else{
                binding.progressCircular.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
                password(email)
            }
        }
    }


    private fun login(email: String, password: String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful){
                binding.progressCircular.visibility = View.GONE
                activity?.onBackPressed()
            } else{
                binding.progressCircular.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
                Toast.makeText(context, "Failed!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun password(email: String){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Reset Link sent", Toast.LENGTH_SHORT).show()
                binding.progressCircular.visibility = View.GONE
                activity?.onBackPressed()
            } else{
                binding.progressCircular.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
                Toast.makeText(context, "Failed!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
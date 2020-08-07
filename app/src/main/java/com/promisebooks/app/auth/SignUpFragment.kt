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
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: SignUpFragmentBinding
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Users")
    var name = " "
    var phone = " "

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
         name = binding.nameInput.text.toString()
            phone = binding.phoneInput.text.toString()
            if (email.isNotEmpty() and password.isNotEmpty() and name.isNotEmpty() and phone.isNotEmpty()){
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
                val user = hashMapOf(
                    "name" to name,
                    "phone" to phone
                )
                val uiid = FirebaseAuth.getInstance().currentUser?.uid
                collection.document(uiid!!).set(user).addOnCompleteListener {it1 ->
                    if (it1.isSuccessful){
                        binding.progressCircular.visibility = View.GONE
                        activity?.onBackPressed()
                    }
                }

            } else{
                binding.progressCircular.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
            }
        }
    }

}
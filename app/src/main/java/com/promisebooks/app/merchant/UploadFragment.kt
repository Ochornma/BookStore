package com.promisebooks.app.merchant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.UploadFragmentBinding
import com.promisebooks.app.model.Book
import com.promisebooks.app.util.K

class UploadFragment : Fragment() {
    private lateinit var binding:UploadFragmentBinding
    private var imageurl = " "
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Books")
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    companion object {
        fun newInstance() = UploadFragment()
    }

    private lateinit var viewModel: UploadViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.upload_fragment, container, false)
        binding.backPress.setOnClickListener {
            activity?.onBackPressed()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UploadViewModel::class.java)
        binding.upload.setOnClickListener {
          val  title = binding.titleInput.text.toString()
            val  descripe = binding.descripInput.text.toString()
            val  price = binding.priceInput.text.toString()
            if (title.isNotEmpty() and descripe.isNotEmpty() and price.isNotEmpty() /*and imageurl.isNotEmpty()*/){
                val book = Book(title, "https://firebasestorage.googleapis.com/v0/b/flutterwave-promise.appspot.com/o/flutterwave%2Fbook1.png?alt=media&token=aa1b28be-06fa-43be-b778-e6fb1f3f33d0", descripe, price)
                collection.document().set(book).addOnSuccessListener {
                    activity?.let { it1 -> K.alert("UPLOADED", null, it1, false) }
                    clear()
                }
            }
        }

    }

    private fun clear() {
        binding.apply {
           titleInput.setText("")
            descripInput.setText("")
            priceInput.setText("")
            invalidateAll()
        }
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
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null){
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                /* val intent = Intent(activity?.applicationContext, AuthActivity::class.java)
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                 activity?.startActivity(intent)
                 activity?.finish()*/
                activity?.let {it1 ->
                    it1.startActivity(
                        Intent(it1, AuthActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    it1.finish()
                }

            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }
}
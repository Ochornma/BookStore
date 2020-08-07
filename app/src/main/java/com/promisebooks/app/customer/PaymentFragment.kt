package com.promisebooks.app.customer

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.auth.AuthActivity
import com.promisebooks.app.databinding.PaymentFragmentBinding
import com.promisebooks.app.model.*
import com.promisebooks.app.util.AccountRecieved
import com.promisebooks.app.util.ProgressCheck
import com.promisebooks.app.util.Transaction
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment(), Transaction, AccountRecieved, ProgressCheck {
    private lateinit var binding: PaymentFragmentBinding
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private var db = FirebaseFirestore.getInstance()
    private var collectionUser = db.collection("Users")
    private var collectionProduct = db.collection("BoughtProducts")
    private var email: String = " "
    private var ref = " "
    private var uiid = " "
    private var name = ""
    private var phone = ""
    var year = Calendar.getInstance().get(Calendar.YEAR)
    var month = Calendar.getInstance().get(Calendar.MONTH)
    var day = Calendar.getInstance().get(Calendar.DATE)

    companion object {
        fun newInstance() = PaymentFragment()
    }

    private lateinit var viewModel: PaymentViewModel
    private lateinit var adapter: PaymentAdapter
    private lateinit var book: Book

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.payment_fragment, container, false)
        val args: PaymentFragmentArgs by navArgs()
        book = args.book

        binding.progressCircular.visibility = View.VISIBLE
        binding.paymentContainer.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        adapter = PaymentAdapter()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val factory = context?.let { PaymentFactory(this, this, it, this) }!!
        viewModel = ViewModelProvider(this, factory).get(PaymentViewModel::class.java)
        viewModel.getBank()
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH)
        val strDate: String = sdf.format(c.time)

        binding.transfer.setOnClickListener {
            binding.progressCircular.visibility = View.VISIBLE
            ref = email + "_" + strDate
            val accounRequest = AccounRequest(
                book.price,
                " ",
                "NGN",
                " ",
                2,
                email,
                5,
                false,
                "Promise Book Stores",
                phone,
                ref
            )
            viewModel.sendBank(accounRequest)
        }

        binding.ussd.setOnClickListener {
            if (binding.emailInput.text.toString().isNotEmpty()){
                binding.progressCircular.visibility = View.VISIBLE
                ref = email + "_" + strDate
                val accounRequest1 = USSDRequest(
                    binding.emailInput.text.toString(),
                    book.price,
                    "NGN",
                    email,
                    name,
                    phone,
                    ref
                )
                viewModel.sendUssd(accounRequest1)
            } else{
                Toast.makeText(context, " Enter Bank Code", Toast.LENGTH_SHORT).show()
            }

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

    private fun setUpListener() {
        authListner = FirebaseAuth.AuthStateListener {
            if (it.currentUser != null) {
               /* if (merchant(it.currentUser!!.email!!)) {
                    val intent = Intent(activity, MerchantActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    activity?.finish()
                }*/
                email = FirebaseAuth.getInstance().currentUser?.email!!
                uiid = FirebaseAuth.getInstance().currentUser?.uid!!
                collectionUser.document(uiid).get().addOnSuccessListener {it1 ->
                    val user = it1.toObject<User>(User::class.java)
                    if (user != null){
                        phone = user.phone
                        name = user.name
                    }


                }
            } else {
                FirebaseAuth.getInstance().removeAuthStateListener(authListner)
                val intent = Intent(activity?.applicationContext, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity?.startActivity(intent)
                activity?.finish()
            }

        }
        FirebaseAuth.getInstance().addAuthStateListener(authListner)
    }

    fun setData() {
        val bookBought = BookBought(book.title, book.image, book.description, book.price, uiid)
        collectionProduct.document(ref).set(bookBought).addOnSuccessListener {
            binding.progressCircular.visibility = View.GONE
            val builder: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
            builder?.setNegativeButton(activity?.resources?.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                // Since we cannot proceed, we should finish the activity
            }
            builder?.setMessage("SUCCESSFUL")
            builder?.create()?.show()
        }
    }

    private fun merchant(email: String): Boolean {
        return (email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
    }

    override fun received(code: String, ref: String) {
        binding.progressCircular.visibility = View.GONE
        val builder: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
        builder?.setPositiveButton("Confirm my Payment"
        ) { dialog, _ ->
            binding.progressCircular.visibility = View.VISIBLE
            viewModel.getProgress("https://api.flutterwave.com/v3/transactions?from=$year-${month+1}-$day&to=$year-${month+1}-$day&tx_ref=$ref", book.price)
            dialog.dismiss()
        }
        builder?.setNegativeButton(activity?.resources?.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            // Since we cannot proceed, we should finish the activity
        }
        builder?.setMessage(code)
        builder?.create()?.show()
    }

    override fun recieved(banks: List<Data1>) {
        adapter.setBank(bank = banks.toMutableList())
        binding.recyclerView.adapter = adapter
        binding.progressCircular.visibility = View.GONE
        binding.paymentContainer.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun progress() {
        setData()
    }

    override fun error() {
        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
    }

}
package com.promisebooks.app.customer

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager
import com.flutterwave.raveandroid.rave_presentation.RavePayManager
import com.flutterwave.raveandroid.rave_presentation.card.Card
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails
import com.flutterwave.raveutils.verification.AVSVBVFragment
import com.flutterwave.raveutils.verification.OTPFragment
import com.flutterwave.raveutils.verification.PinFragment
import com.flutterwave.raveutils.verification.RaveVerificationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.promisebooks.app.R
import com.promisebooks.app.customer.adapter.PaymentAdapter
import com.promisebooks.app.databinding.CartPaymentFragmentBinding
import com.promisebooks.app.model.AccounRequest
import com.promisebooks.app.model.Cart
import com.promisebooks.app.model.Data1
import com.promisebooks.app.model.USSDRequest
import com.promisebooks.app.util.*
import java.util.*

class CartPaymentFragment : BaseFragment2<CartPaymentFragmentBinding>(), Transaction, AccountRecieved, ProgressCheck, CardPaymentCallback, CartDeleteCalback {
    private lateinit var authListner: FirebaseAuth.AuthStateListener
    private var db = FirebaseFirestore.getInstance()

    private var email: String = " "
    private var ref = " "

    private var name = ""
    private var phone = ""
    private var year = Calendar.getInstance().get(Calendar.YEAR)
    private var month = Calendar.getInstance().get(Calendar.MONTH)
    private var day = Calendar.getInstance().get(Calendar.DATE)
    private lateinit var util: RaveVerificationUtils
    private var cardPayManager: CardPaymentManager? = null
    private var card: Card? = null
    private var raveManager: RavePayManager? = null
    private lateinit var adapter: PaymentAdapter
    private lateinit var cart: Cart
    private var price = 0
    companion object {
        fun newInstance() = CartPaymentFragment()
    }

    private lateinit var viewModel: CartPaymentViewModel


    override fun setUpViews() {
        super.setUpViews()
        val args: CartPaymentFragmentArgs by navArgs()
        cart = args.cart
        binding.quantityInput.setText("${cart.qty}")
        util = RaveVerificationUtils(this, true, "FLWPUBK_TEST-d38161cd5980f1e8e447620609620afa-X")
        binding.progressCircular.visibility = View.VISIBLE
        binding.paymentContainer.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        adapter = PaymentAdapter()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        binding.transfer.setOnClickListener {
            price = binding.quantityInput.text.toString().toInt() * cart.price.toInt()
            binding.progressCircular.visibility = View.VISIBLE
            ref = email + "_" + viewModel.getTime()
            val accounRequest = AccounRequest(
                "$price",
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
            price = binding.quantityInput.text.toString().toInt() * cart.price.toInt()
            if (binding.emailInput.text.toString().isNotEmpty()){
                binding.progressCircular.visibility = View.VISIBLE
                ref = email + "_" + viewModel.getTime()
                val accounRequest1 = USSDRequest(
                    binding.emailInput.text.toString(),
                    "$price",
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

        binding.card.setOnClickListener {
            binding.recyclerView.visibility = View.GONE
            binding.progressCircular.visibility = View.GONE
            binding.cardLayout.visibility = View.VISIBLE
            binding.paymentContainer.visibility = View.GONE
        }
        binding.cancel.setOnClickListener {
            binding.recyclerView.visibility = View.VISIBLE
            binding.progressCircular.visibility = View.GONE
            binding.cardLayout.visibility = View.GONE
            binding.paymentContainer.visibility = View.VISIBLE
            clear()
        }

        binding.submit.setOnClickListener {
            price = binding.quantityInput.text.toString().toInt() * cart.price.toInt()
            ref = email + "_" + viewModel.getTime()
            raveManager = RaveNonUIManager().setAmount(price.toDouble())
                .setCurrency("NGN")
                .setEmail(email)
                .setPhoneNumber(phone)
                .setfName(binding.firstNameInput.text.toString())
                .setlName(binding.lastNameInput.text.toString())
                .setTxRef(ref)
                .setNarration("Promise Book Store")
                .setEncryptionKey("FLWSECK_TEST6de42121a4d6")
                .setPublicKey("FLWPUBK_TEST-d38161cd5980f1e8e447620609620afa-X")
                .onStagingEnv(true)
                .initialize()
            cardPayManager = CardPaymentManager(raveManager as RaveNonUIManager?, this, null)
            card = Card(
                binding.cardInput.text.toString(),
                binding.monthInput.text.toString(),
                binding.yearInput.text.toString(),
                binding.cvvInput.text.toString()
            )
            cardPayManager!!.chargeCard(card)
        }

    }

    override fun setUpViewModel() {
        super.setUpViewModel()
        val factory = context?.let { CartPaymentFactory(this, this, it, this) }!!
        viewModel = ViewModelProvider(this, factory).get(CartPaymentViewModel::class.java)
        viewModel.getBank()
    }


    private fun clear(){
        binding.apply {
            cardInput.setText("")
            monthInput.setText("")
            yearInput.setText("")
            cardInput.setText("")
            cvvInput.setText("")
            firstNameInput.setText("")
            lastNameInput.setText("")
            quantityInput.setText("")
            invalidateAll()
        }
    }


    private fun setData() {
        if (user != null){
            viewModel.setData(cart, ref, user!!.uid, user!!.email!!, this)
        }

    }

/*    private fun merchant(email: String): Boolean {
        return (email.substring(email.indexOf("@") + 1).toLowerCase(Locale.ROOT)) == "merchant.com"
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RaveConstants.RESULT_SUCCESS) {
            when (requestCode) {
                RaveConstants.PIN_REQUEST_CODE -> {
                    val pin = data!!.getStringExtra(PinFragment.EXTRA_PIN)
                    // Use the collected PIN
                    cardPayManager!!.submitPin(pin)
                }
                RaveConstants.ADDRESS_DETAILS_REQUEST_CODE -> {
                    val streetAddress =
                        data!!.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS)
                    val state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE)
                    val city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY)
                    val zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE)
                    val country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY)
                    val address = AddressDetails(streetAddress, city, state, zipCode, country)

                    // Use the address details
                    cardPayManager!!.submitAddress(address)
                }
                RaveConstants.WEB_VERIFICATION_REQUEST_CODE ->                     // Web authentication complete, proceed
                    cardPayManager!!.onWebpageAuthenticationComplete()
                RaveConstants.OTP_REQUEST_CODE -> {
                    val otp = data!!.getStringExtra(OTPFragment.EXTRA_OTP)
                    // Use OTP
                    cardPayManager!!.submitOtp(otp)
                }
            }
        }
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            if (message != null) {
                Log.d("rave response", message)
            }
            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                   // verifyPay("SUCCESS $message", ref)
                    //Toast.makeText(context, "SUCCESS $message", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(context, "ERROR $message", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(context, "CANCELLED $message", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == RaveConstants.WEB_VERIFICATION_REQUEST_CODE) {
            cardPayManager!!.onWebpageAuthenticationComplete()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun verifyPay(message: String, ref: String){
        binding.progressCircular.visibility = View.GONE
        val builder: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
        builder?.setPositiveButton("Confirm my Payment"
        ) { dialog, _ ->
            binding.progressCircular.visibility = View.VISIBLE
            viewModel.getProgress("https://api.flutterwave.com/v3/transactions?from=$year-${month+1}-$day&to=$year-${month+1}-$day&tx_ref=$ref", "$price")
            dialog.dismiss()
        }
        builder?.setNegativeButton(activity?.resources?.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            // Since we cannot proceed, we should finish the activity
        }
        builder?.setMessage(message)
        builder?.create()?.show()
    }
    override fun received(code: String, ref: String) {
        verifyPay(code, ref)
    }

    override fun errorTransaction() {
        activity?.let { BookView.alert("error", binding.progressCircular, it, false) }
    }

    override fun recieved(banks: List<Data1>) {
        adapter.setBank(bank = banks.toMutableList())
        binding.recyclerView.adapter = adapter
        binding.progressCircular.visibility = View.GONE
        binding.paymentContainer.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun errorAccount() {
        activity?.let {
            BookView.alert(
                "Couldn't Retrieve Bank Codes",
                binding.progressCircular,
                it,
                false
            )
        }

    }

    override fun progress() {
        setData()
    }

    override fun errorProgress() {
        activity?.let { BookView.alert("error", binding.progressCircular, it, false) }
    }

    override fun collectAddress() {
        Toast.makeText(context, "Please Wait", Toast.LENGTH_SHORT).show()
        util.showAddressScreen()
    }

    override fun onSuccessful(flwRef: String?) {
        setData()
    }

    override fun showProgressIndicator(active: Boolean) {
        if (active){
            binding.progressCircular.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            binding.cardLayout.visibility = View.GONE
            binding.paymentContainer.visibility = View.GONE
        } else{
            binding.progressCircular.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            binding.cardLayout.visibility = View.VISIBLE
            binding.paymentContainer.visibility = View.VISIBLE
        }

    }

    override fun showAuthenticationWebPage(authenticationUrl: String?) {
        Toast.makeText(context, "Please Wait", Toast.LENGTH_SHORT).show()
        util.showWebpageVerificationScreen(authenticationUrl)
    }

    override fun collectOtp(message: String?) {
        util.showOtpScreen("Enter your OTP")
    }

    override fun onError(errorMessage: String?, flwRef: String?) {
        activity?.let { BookView.alert("error", binding.progressCircular, it, false) }
    }

    override fun collectCardPin() {
        util.showPinScreen()
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CartPaymentFragmentBinding {
        return CartPaymentFragmentBinding.inflate(inflater, container, false)

    }

    override fun deleteCallback() {
        binding.progressCircular.visibility = View.GONE
        val builder: AlertDialog.Builder = context.let { AlertDialog.Builder(it) }
        builder.setNegativeButton(activity?.resources?.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            // Since we cannot proceed, we should finish the activity
        }
        builder.setMessage("SUCCESSFUL")
        builder.create()?.show()
    }

}
package com.promisebooks.app.auth

import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
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
import com.promisebooks.app.databinding.SignUpFragmentBinding
import com.promisebooks.app.util.BookRepo
import com.promisebooks.app.util.K
import com.promisebooks.app.util.ProgressCheck
import java.text.SimpleDateFormat
import java.util.*

class SignUpFragment : Fragment(), CardPaymentCallback, ProgressCheck {
    private lateinit var binding: SignUpFragmentBinding
    private var db = FirebaseFirestore.getInstance()
    private var collection = db.collection("Users")
    private var email: String = " "
    private var ref = " "
    private var name = ""
    private var password = ""
    private var phone = ""
    private var yearCard = ""
    private var monthCard = ""
    private var cardInput = ""
    private var cvv = ""
    private var lname = ""
    private var fname = ""
    private var year = Calendar.getInstance().get(Calendar.YEAR)
    private var month = Calendar.getInstance().get(Calendar.MONTH)
    private var day = Calendar.getInstance().get(Calendar.DATE)
    private lateinit var util: RaveVerificationUtils
    private var cardPayManager: CardPaymentManager? = null
    private var card: Card? = null
    private var raveManager: RavePayManager? = null
    val c = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.ENGLISH)
    val strDate: String = sdf.format(c.time)

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false)
        util = RaveVerificationUtils(this, false, "FLWPUBK-b6f6a82a3ff8ba0fbfcaa5a99a6bec04-X")
        binding.progressCircular.visibility = View.VISIBLE
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        binding.backPress.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.submit.setOnClickListener {
            email = binding.emailInput.text.toString()
            password = binding.passwordInput.text.toString()
            ref = email + "_" + strDate
            lname = binding.lnameInput.text.toString()
            fname = binding.fnameInput.text.toString()
            name = "$fname $lname"
            yearCard = binding.yearInput.text.toString()
            monthCard = binding.monthInput.text.toString()
            cardInput = binding.cardInput.text.toString()
            cvv = binding.cvvInput.text.toString()
            phone = binding.phoneInput.text.toString()

            if (email.isNotEmpty() and password.isNotEmpty() and lname.isNotEmpty() and phone.isNotEmpty()
                and fname.isNotEmpty() and yearCard.isNotEmpty() and monthCard.isNotEmpty()
                and cardInput.isNotEmpty() and cvv.isNotEmpty() and phone.isNotEmpty()
            ) {

                binding.progressCircular.visibility = View.VISIBLE
                binding.cardView.visibility = View.GONE
                raveManager = RaveNonUIManager().setAmount(100.00)
                    .setCurrency("NGN")
                    .setEmail(email)
                    .setPhoneNumber(phone)
                    .setfName(fname)
                    .setlName(lname)
                    .setTxRef(ref)
                    .setNarration("Promise Book Store")
                    .setEncryptionKey("2047ac195430353ccfdfa252")
                    .setPublicKey("FLWPUBK-b6f6a82a3ff8ba0fbfcaa5a99a6bec04-X")
                    .onStagingEnv(false)
                    .initialize()
                cardPayManager = CardPaymentManager(raveManager as RaveNonUIManager?, this, null)
                card = Card(
                    cardInput,
                    monthCard,
                    yearCard,
                    cvv
                )
                cardPayManager!!.chargeCard(card)
                // signUp(email = email, password = password)
            } else {
                Toast.makeText(context, "No Field should be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun signUp(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = hashMapOf(
                        "name" to name,
                        "phone" to phone
                    )
                    val uiid = FirebaseAuth.getInstance().currentUser?.uid
                    collection.document(uiid!!).set(user).addOnCompleteListener { it1 ->
                        if (it1.isSuccessful) {
                            binding.progressCircular.visibility = View.GONE
                            activity?.onBackPressed()
                        }
                    }

                } else {
                    binding.progressCircular.visibility = View.GONE
                    binding.cardView.visibility = View.VISIBLE
                }
            }
    }

    override fun onStart() {
        super.onStart()
        alert()
    }

    private fun alert() {

        val builder: AlertDialog.Builder? = context.let { AlertDialog.Builder(it) }
        builder?.setNegativeButton(context?.resources?.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            activity?.onBackPressed()
        }
        builder?.setPositiveButton("Proceed") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder?.setMessage("Sign up requires a non-refundable fee of NGN 100")
        builder?.create()?.show()
    }

    private fun verifyPay(message: String, ref: String){
        binding.progressCircular.visibility = View.GONE
        val builder: AlertDialog.Builder? = context?.let { AlertDialog.Builder(it) }
        builder?.setPositiveButton("Confirm my Payment"
        ) { dialog, _ ->
            binding.progressCircular.visibility = View.VISIBLE
            BookRepo(this).getTransaction("https://api.flutterwave.com/v3/transactions?from=$year-${month+1}-$day&to=$year-${month+1}-$day&tx_ref=$ref", "100", true)
            dialog.dismiss()
        }
        builder?.setNegativeButton(activity?.resources?.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            // Since we cannot proceed, we should finish the activity
        }
        builder?.setMessage(message)
        builder?.create()?.show()
    }

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
                    verifyPay("SUCCESS $message", ref)
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


    override fun collectAddress() {
        Toast.makeText(context, "Please Wait", Toast.LENGTH_SHORT).show()
        util.showAddressScreen()
    }

    override fun onSuccessful(flwRef: String?) {
        signUp(email, password)
    }

    override fun showProgressIndicator(active: Boolean) {
        if (active) {
            binding.progressCircular.visibility = View.VISIBLE
            binding.cardView.visibility = View.GONE

        } else {
            binding.progressCircular.visibility = View.GONE
            binding.cardView.visibility = View.VISIBLE

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
        activity?.let { K.alert("error", binding.progressCircular, it, false) }
    }

    override fun collectCardPin() {
        util.showPinScreen()
    }

    override fun progress() {
       signUp(email, password)
    }

    override fun errorProgress() {
        activity?.let { K.alert("error", binding.progressCircular, it, false) }
    }
}

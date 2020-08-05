package com.promisebooks.app.customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.promisebooks.app.R


class MyBookFragment : Fragment() {

    companion object {
        fun newInstance() = MyBookFragment()
    }

    private lateinit var viewModel: MyBookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_book_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyBookViewModel::class.java)
        RaveUiManager(activity).setAmount(100.0)
            .setCurrency("NGN")
            .setEmail("ochornmapromise@gmail.com")
            .setfName("fName")
            .setlName("lName")
            .setNarration("narration")
            .setPublicKey("FLWPUBK_TEST-d38161cd5980f1e8e447620609620afa-X")
            .setEncryptionKey("FLWSECK_TEST6de42121a4d6")
            .setTxRef("txRef")
            .acceptAccountPayments(true)
            .acceptUssdPayments(true)

            .allowSaveCardFeature(false)
            .onStagingEnv(true)

            .shouldDisplayFee(true)
            .initialize()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        /*
     *  We advise you to do a further verification of transaction's details on your server to be
     *  sure everything checks out before providing service or goods.
    */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            val message = data.getStringExtra("response")
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(context, "SUCCESS $message", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(context, "ERROR $message", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(context, "CANCELLED $message", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}


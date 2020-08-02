package com.promisebooks.app.merchant

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.promisebooks.app.R

class RefundFragment : Fragment() {

    companion object {
        fun newInstance() = RefundFragment()
    }

    private lateinit var viewModel: RefundViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.refund_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RefundViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
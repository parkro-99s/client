package com.parkro.client.domain.payment.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentPaymentBinding
import com.parkro.client.domain.map.ui.MapViewModel

class PaymentFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentPaymentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        mapViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // Update toolbar title from fragment
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_payment), true, false)

        // Find the button and set an onClickListener
        val btnToPaymentWebView: Button = binding.btnToPaymentWebView
        btnToPaymentWebView.setOnClickListener {
            // Start PaymentWebViewActivity
            val intent = Intent(activity, PaymentWebViewActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.parkro.client.domain.example.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentExampleBinding
import com.parkro.client.domain.example.viewmodel.ExampleViewModel

class ExampleFragment : Fragment() {

    private lateinit var exampleViewModel: ExampleViewModel
    private var _binding: FragmentExampleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        exampleViewModel = ViewModelProvider(this).get(ExampleViewModel::class.java)
        _binding = FragmentExampleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // ViewModel에서 지정해준 LiveData 가져오기
        exampleViewModel.exampleResult.observe(viewLifecycleOwner, Observer { exampleDTO ->
            exampleDTO?.let {
                // UI(textExample)에 결과값 출력
                val sb = StringBuilder()
                sb.append("Receipt ID: ${exampleDTO.receiptId}\n")
                    .append("Store ID: ${exampleDTO.storeId}\n")
                    .append("Total Price: ${exampleDTO.totalPrice}\n")
                    .append("Status: ${exampleDTO.status}\n")
                    .append("Created Date: ${exampleDTO.createdDate}\n")
                binding.textExample.text = sb.toString()
            } ?: run {
                binding.textExample.text = "Error loading data"
            }
        })

        exampleViewModel.fetchMemberReceiptInfo(3)

        // Update the toolbar title
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_example), false, false)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

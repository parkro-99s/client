package com.parkro.client.domain.parkinglist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentParkinglistBinding

class ParkingListFragment : Fragment() {

    private lateinit var parkingListViewModel: ParkingListViewModel
    private var _binding: FragmentParkinglistBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parkingListViewModel =
            ViewModelProvider(this).get(ParkingListViewModel::class.java)

        _binding = FragmentParkinglistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        parkingListViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_parkinglist), false, true)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
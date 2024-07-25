package com.parkro.client.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {
    private lateinit var mypageViewModel: MypageViewModel
    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mypageViewModel =
            ViewModelProvider(this).get(MypageViewModel::class.java)

        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMypage
        mypageViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // Update toolbar title from fragment
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_mypage), true, false)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
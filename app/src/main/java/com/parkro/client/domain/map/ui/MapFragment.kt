package com.parkro.client.domain.map.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap

import com.kakao.vectormap.KakaoMapReadyCallback

import com.kakao.vectormap.MapLifeCycleCallback

import com.parkro.client.R
import android.util.Log
import androidx.annotation.NonNull
import com.kakao.vectormap.MapView
import java.lang.Exception


class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMap
        mapViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // toolbar title 수정
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_map), false, false)



        // map
        mapView = binding.mapView
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출
                Log.d("KakaoMap", "onMapDestroy: ")
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출
                Log.e("KakaoMap", "onMapError: ", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(@NonNull map: KakaoMap) {
                // 정상적으로 인증이 완료되었을 때 호출
                // KakaoMap 객체를 얻어 옵니다.
                kakaoMap = map
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
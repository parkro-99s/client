package com.parkro.client.domain.map.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.kakao.vectormap.*
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.kakao.vectormap.label.*
import com.parkro.client.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var zoomlevel = 17

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap

    private lateinit var layer: LabelLayer
    private lateinit var centerLabel: Label
    private var startPosition: LatLng? = null

    private var requestingLocationUpdates = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapView = binding.mapMap

        // toolbar title 수정
        (activity as? MainActivity)?.updateToolbarTitle("", false, false)

        // FusedLocationProviderClient 인스턴스를 가져옴
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 2초마다 최고 정확도로 업데이트를 요청하는 LocationRequest 객체를 생성
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()

        // 위치가 업데이트 될때마다 실행될 LocationCallback 메서드를 생성
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val latLng = LatLng.from(location.latitude, location.longitude)
                    if (::centerLabel.isInitialized) {
                        centerLabel.moveTo(latLng)
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(requireContext(), locationPermissions[0]) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), locationPermissions[1]) == PackageManager.PERMISSION_GRANTED) {
            getStartLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                locationPermissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 해당 메서드를 위치 권한 획득한 이후에 실행되도록 하기
    @SuppressLint("MissingPermission")
    private fun getStartLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    startPosition = LatLng.from(it.latitude, it.longitude)
                    startMap()
                }
            }
    }

    private fun startMap() {
        mapView.start(object : KakaoMapReadyCallback() {

            override fun onMapReady(kakaoMap: KakaoMap) {
                this@MapFragment.kakaoMap = kakaoMap
                layer = kakaoMap.labelManager?.layer!!

                startPosition?.let { position ->
                    val options = LabelOptions.from("centerLabel", position)
                        .setStyles(LabelStyle.from(R.drawable.marker_car).setAnchorPoint(0.5f, 0.5f))
                        .setRank(1)
                    centerLabel = layer.addLabel(options)

                    // 라벨의 위치가 변하더라도 항상 화면 중앙에 위치할 수 있도록 trackingManager를 통해 tracking을 시작
                    kakaoMap.trackingManager?.startTracking(centerLabel)
                    startLocationUpdates()
                }
            }

            override fun getPosition(): LatLng {
                return startPosition ?: LatLng.from(0.0, 0.0)
            }

            override fun getZoomLevel(): Int {
                return zoomlevel
            }
        })
    }

    // 위치 업데이트 요청을 시작하는 메서드
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        requestingLocationUpdates = true
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStartLocation()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("위치 권한 거부 시 앱을 사용할 수 없습니다.")
            .setPositiveButton("권한 설정하러 가기") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:${requireContext().packageName}"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                } finally {
                    activity?.finish()
                }
            }
            .setNegativeButton("앱 종료하기") { _, _ ->
                activity?.finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

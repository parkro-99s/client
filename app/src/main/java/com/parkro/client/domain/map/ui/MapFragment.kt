package com.parkro.client.domain.map.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.parkro.client.R
import com.parkro.client.domain.map.api.GetParkingLotRes
import com.kakao.vectormap.label.*
import com.parkro.client.databinding.FragmentMapBinding
import com.parkro.client.util.ClipboardUtil
import com.parkro.client.util.PermissionUtil
import com.parkro.client.util.PreferencesUtil

class MapFragment : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private var zoomlevel = 17

    // 클릭된 버튼 추적
    private var currentSelectedButton: ImageButton? = null
    private var currentSelectedTextView: TextView? = null

    private lateinit var kakaoMap: KakaoMap
    private lateinit var layer: LabelLayer
    private lateinit var centerLabel: Label
    private var startPosition: LatLng? = null

    private var requestingLocationUpdates = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val labels = mutableListOf<Label>()
    private lateinit var permissionUtil: PermissionUtil

    // 권한 요청 결과 처리
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // 모든 권한이 허용된 경우
            getStartLocation()
        } else {
            // 하나 이상의 권한이 거부된 경우
            Toast.makeText(requireContext(), "지도를 조회하려면 위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

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

        // 권한 확인 및 요청
        permissionUtil = PermissionUtil(requireContext(), requestPermissionsLauncher)
        permissionUtil.checkLocationPermissions {
            getStartLocation()
        }

        setupButtons()
        binding.recyclerviewMapList.layoutManager = LinearLayoutManager(requireContext())  // 리사이클러뷰

        observeViewModel()

        return root
    }

    private fun setupButtons() {
        binding.btnMapSpace1.setOnClickListener {
            onStoreButtonClick(it as ImageButton, binding.textMapSpace1, "1")
        }
        binding.btnMapDongdaemoon.setOnClickListener {
            onStoreButtonClick(it as ImageButton, binding.textMapDongdaemoon, "2")
        }
        binding.btnMapPangyo.setOnClickListener {
            onStoreButtonClick(it as ImageButton, binding.textMapPangyo, "3")
        }
        binding.btnMapDaegu.setOnClickListener {
            onStoreButtonClick(it as ImageButton, binding.textMapDaegu, "4")
        }
        binding.btnMapMycar.setOnClickListener {
            trackMyLocation()
        }
    }

    private fun onStoreButtonClick(button: ImageButton, textView: TextView, storeId: String) {
        // 이전에 선택된 버튼 색상 변경
        currentSelectedButton?.setImageResource(R.drawable.btn_map_store_white)
        currentSelectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.parkro_black))

        // 현재 선택된 버튼 색상 변경
        button.setImageResource(R.drawable.btn_map_store_navy)
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.parkro_white))

        // 현재 선택된 버튼과 텍스트를 업데이트
        currentSelectedButton = button
        currentSelectedTextView = textView

        // 주차장 데이터 로드
        mapViewModel.fetchParkingLotData(storeId)
    }

    private fun observeViewModel() {
        mapViewModel.parkingLots.observe(viewLifecycleOwner) { result ->
            result.onSuccess { parkingLots ->
                // 기존 라벨 제거
                removeExistingLabels()
                // 트랙킹 중지
                kakaoMap.trackingManager?.stopTracking()

                // 새로운 라벨 추가 및 isInternal == "Y" 인 주차장 찾기
                var internalParkingLot: GetParkingLotRes? = null
                for (parkingLot in parkingLots) {
                    addParkingLotLabel(parkingLot)
                    if (parkingLot.isInternal == "Y" && internalParkingLot == null) {
                        internalParkingLot = parkingLot
                    }
                }

                // 첫번째 주차장 표시
                val firstParkingLot = parkingLots[0]
                binding.textMapFirstItemParkingLotName.text = firstParkingLot.name
                binding.textMapFirstItemAddress.text = firstParkingLot.address
                binding.textMapFirstItemSpaces.text = "${firstParkingLot.usedSpaces}/${firstParkingLot.totalSpaces}"

                binding.textMapFirstItemAddress.text =
                    binding.textMapFirstItemAddress.text.toString().replace(" ", "\u00A0")

                // 클립보드 복사
                binding.btnMapFirstCopy.setOnClickListener {
                    ClipboardUtil.copyTextToClipboard(requireContext(),
                        binding.textMapFirstItemAddress.text.toString(),
                        "주소가 클립보드에 복사되었습니다.")
                }

                // RecyclerView에 데이터 표시
                val otherParkingLots = parkingLots.subList(1, parkingLots.size)
                binding.recyclerviewMapList.adapter = ParkingLotRecyclerAdapter(otherParkingLots)

                // isInternal == "Y" 인 주차장이 있으면 화면 중앙으로 이동
                internalParkingLot?.let {
                    kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(it.latitude, it.longitude)))
                }
            }.onFailure { exception ->
                Log.e("MapFragment", "Error fetching parking lots", exception)
                Toast.makeText(context, "주차장을 찾을 수 없습니다. 통신 상태를 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }

        mapViewModel.errorState.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun trackMyLocation() {
        if (::centerLabel.isInitialized) {
            kakaoMap.trackingManager?.startTracking(centerLabel)
        } else {
            Toast.makeText(context, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun initialSetup() {
        // 초기 상태에서 btnMapSpace1이 선택된 것처럼 설정
        currentSelectedButton = binding.btnMapSpace1
        currentSelectedTextView = binding.textMapSpace1
        currentSelectedButton?.setImageResource(R.drawable.btn_map_store_navy)
        currentSelectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.parkro_white))

        // 주차장 데이터 로드
        if (::kakaoMap.isInitialized) {
            mapViewModel.fetchParkingLotData("1")
        }
    }

    private fun addParkingLotLabel(parkingLot: GetParkingLotRes) {
        if (!::kakaoMap.isInitialized) {
            return
        }

        val position = LatLng.from(parkingLot.latitude, parkingLot.longitude)
        val options = LabelOptions.from(parkingLot.name, position)
            .setStyles(LabelStyle.from(R.drawable.marker_red).setAnchorPoint(0.5f, 0.5f))
            .setRank(1)
        val label = layer.addLabel(options)
        labels.add(label)
    }

    private fun removeExistingLabels() {
        for (label in labels) {
            layer.remove(label)
        }
        labels.clear()
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
                    val carProfile = PreferencesUtil.getCarProfile()
                    val markerDrawable = when (carProfile) {
                        1 -> R.drawable.marker_car_orange
                        2 -> R.drawable.marker_car_blue
                        3 -> R.drawable.marker_car_yellow
                        4 -> R.drawable.marker_car_red
                        5 -> R.drawable.marker_car_sky
                        else -> R.drawable.marker_car_orange
                    }

                    val options = LabelOptions.from("centerLabel", position)
                        .setStyles(LabelStyle.from(markerDrawable).setAnchorPoint(0.5f, 0.5f))
                        .setRank(1)
                    centerLabel = layer.addLabel(options)

                    // 라벨의 위치가 변하더라도 항상 화면 중앙에 위치할 수 있도록 trackingManager를 통해 tracking을 시작
                    kakaoMap.trackingManager?.startTracking(centerLabel)
                    startLocationUpdates()
                }

                // 초기 데이터 로드 (예: storeId "1")
                initialSetup()
            }

            override fun getPosition(): LatLng {
                return startPosition ?: LatLng.from(0.0, 0.0)
            }

            override fun getZoomLevel(): Int {
                return zoomlevel
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        requestingLocationUpdates = true
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 클립보드 복사
    public fun copyTextToClipboard(context: Context, text: String, message: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("주소", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

package com.parkro.client.util

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.activity.result.ActivityResultLauncher

/**
 * 요청 권한 설정
 *
 * @author 김민정
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   김민정       최초 생성
 * </pre>
 */
class PermissionUtil(private val context: Context, private val launcher: ActivityResultLauncher<Array<String>>) {

    // 요청할 위치 권한 목록
    private val locationPermissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // 권한 여부에 따른 승인 or 요청
    fun checkLocationPermissions(onGranted: () -> Unit) {
        if (hasLocationPermissions()) {
            onGranted()
        } else {
            requestLocationPermissions()
        }
    }

    // 권한 여부 확인
    private fun hasLocationPermissions(): Boolean {
        return locationPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    // 권한 요청
    private fun requestLocationPermissions() {
        launcher.launch(locationPermissions)
    }
}

package com.opensource.selectsaveimagepicker.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.Manifest
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class PermissionRequester(
	private val fragment: Fragment,
	private val permission: String
) {
	
	private lateinit var permissionLauncher: ActivityResultLauncher<String>
	var onPermissionChecked: (() -> Unit)? = null
	
	private val rationale: String
		get() = when (permission) {
			Manifest.permission.READ_EXTERNAL_STORAGE -> "Access to storage is necessary to load and save images."
			Manifest.permission.READ_MEDIA_IMAGES -> "Access to images is necessary to select and save images."
			else -> "Permission is required to use this feature."
		}
	
	init {
		setupPermissionLauncher()
	}
	
	private fun setupPermissionLauncher() {
		permissionLauncher = fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
			handlePermissionResult(isGranted)
		}
	}
	
	fun requestPermission() {
			permissionLauncher.launch(permission)
	}
	
	private fun handlePermissionResult(isGranted: Boolean) {
		if (isGranted) {
			showSnackbar("Permission Granted")
		} else {
			if (fragment.shouldShowRequestPermissionRationale(permission)) {
				Log.d("Show", "2")
				showRationale()
			} else {
				showSettingsRedirect()
			}
		}
		onPermissionChecked?.invoke()
	}
	
	private fun showRationale() {
		Snackbar.make(fragment.requireView(), rationale, Snackbar.LENGTH_INDEFINITE)
			.setAction("OK") {
				permissionLauncher.launch(permission)
			}.show()
	}
	
	private fun showSettingsRedirect() {
		Snackbar.make(fragment.requireView(), "Permission Denied. You can enable it from settings.", Snackbar.LENGTH_LONG)
			.setAction("Settings") {
				val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
				val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
				intent.data = uri
				fragment.startActivity(intent)
			}.show()
	}
	
	private fun showSnackbar(message: String) {
		Snackbar.make(fragment.requireView(), message, Snackbar.LENGTH_SHORT).show()
	}
}

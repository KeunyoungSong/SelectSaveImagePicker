package com.opensource.selectsaveimagepicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.opensource.selectsaveimagepicker.adapter.GridSpacingItemDecoration
import com.opensource.selectsaveimagepicker.adapter.ImagePickerAdapter
import com.opensource.selectsaveimagepicker.databinding.FragmentSelectSaveImagePickerBinding
import com.opensource.selectsaveimagepicker.repository.ImageRepository
import com.opensource.selectsaveimagepicker.utils.PermissionRequester
import com.opensource.selectsaveimagepicker.utils.logMemoryUsageInKB
import com.opensource.selectsaveimagepicker.utils.logMemoryUsageInMB
import com.opensource.selectsaveimagepicker.viewmodel.ImagePickerEvent
import com.opensource.selectsaveimagepicker.viewmodel.SelectSaveImagePickerViewModel
import com.opensource.selectsaveimagepicker.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class SelectSaveImagePicker(
	private val spacingPx: Int = 8
) : BottomSheetDialogFragment() {
	
	
	private val TAG = "SelectSaveImagePicker"
	
	private var _binding: FragmentSelectSaveImagePickerBinding? = null
	private val binding get() = _binding!!
	
	private val viewModel: SelectSaveImagePickerViewModel by activityViewModels {
		ViewModelFactory(
			maxSelection = 20,
			repository = ImageRepository(requireContext())
		)
	}
	
	private lateinit var imagePickerAdapter: ImagePickerAdapter
	private lateinit var permissionRequester: PermissionRequester
	
	private var spanCount: Int = 3
	
	override fun onAttach(context: Context) {
		super.onAttach(context)
		Log.d(
			TAG,
			"onAttach"
		)
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.d(
			TAG,
			"onCreate"
		)
		logMemoryUsageInKB()
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		Log.d(
			TAG,
			"onCreateView"
		)
		_binding = FragmentSelectSaveImagePickerBinding.inflate(
			inflater,
			container,
			false
		)
		return binding.root
	}
	
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(
			view,
			savedInstanceState
		)
		Log.d(
			TAG,
			"onViewCreated"
		)
		
		if (isLandscape()) {
			spanCount = 5
		} else {
			spanCount = 3
		}
		
		setupPermissionHandling()
		initView()
		initListener()
		initViewModel()
		
		logMemoryUsageInKB()
	}
	
	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		Log.d(
			TAG,
			"onActivityCreated"
		)
	}
	
	override fun onStart() {
		super.onStart()
		Log.d(
			TAG,
			"onStart"
		)
	}
	
	override fun onResume() {
		super.onResume()
		Log.d(
			TAG,
			"onResume"
		)
		checkPermissionsAndUpdateVisibility()
	}
	
	override fun onPause() {
		super.onPause()
		Log.d(
			TAG,
			"onPause"
		)
	}
	
	override fun onStop() {
		super.onStop()
		Log.d(
			TAG,
			"onStop"
		)
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		Log.d(
			TAG,
			"onDestroyView"
		)
		_binding = null
	}
	
	override fun onDestroy() {
		super.onDestroy()
		Log.d(
			TAG,
			"onDestroy"
		)
	}
	
	override fun onDetach() {
		super.onDetach()
		Log.d(
			TAG,
			"onDetach"
		)
	}
	
	private fun initViewModel() {
		lifecycleScope.launch {
			viewModel.images.collect { images ->
				Log.d(
					"RV",
					"${images.take(5).map { it.isSelected }}"
				)
				Log.d(
					"COUNT",
					"${images.size}"
				)
				imagePickerAdapter.submitList(images.toList())
			}
		}
		lifecycleScope.launch {
			viewModel.selectedImagesCount.collect { count ->
				updateAddButton(count)
			}
		}
		lifecycleScope.launch {
			viewModel.event.collect { event ->
				onEvent(event)
			}
		}
	}
	
	private fun onEvent(event: ImagePickerEvent) {
		when (event) {
			ImagePickerEvent.ReachedMaxSelection -> showMaxSelectionDialog()
			else -> Unit
		}
	}
	
	private fun updateAddButton(count: Int) {
		val isEnabled = count != 0
		val color = when (isEnabled) {
			true -> {
				ContextCompat.getColor(
					requireContext(),
					R.color.button_enabled
				)
			}
			false -> {
				ContextCompat.getColor(
					requireContext(),
					R.color.button_disabled
				)
			}
		}
		binding.tvAddButtonText.setTextColor(color)
		
		binding.tvAddCount.apply {
			isVisible = count != 0
			text = if (isEnabled) count.toString() else ""
		}
	}
	
	
	private fun initView() {
		setupImagePickerAdapter()
	}
	
	private fun setupImagePickerAdapter() = with(binding) {
		imagePickerAdapter = ImagePickerAdapter {
			viewModel.handleEvent(ImagePickerEvent.ToggleImage(it))
		}
		rvImages.layoutManager = GridLayoutManager(
			context,
			spanCount
		)
		GridSpacingItemDecoration(
			spanCount,
			spacingPx
		).also {
			binding.rvImages.addItemDecoration(it)
		}
		rvImages.adapter = imagePickerAdapter
		binding.rvImages.setHasFixedSize(true)
	}
	
	private fun initListener() {
		binding.btnRequestPermission.setOnClickListener {
			permissionRequester.requestPermission()
		}
		binding.layoutImagesSwipeRefresh.apply {
			isEnabled = false
			setOnRefreshListener {
				viewModel.loadImages()
				isRefreshing = false
			}
		}
		binding.tvAddButton.setOnClickListener {
			Log.d(
				"Button",
				"Add clicked"
			)
		}
		
		binding.tvCloseButton.setOnClickListener {
			Log.d(
				"Button",
				"Close clicked"
			)
		}
	}
	
	private fun setupPermissionHandling() {
		val permission = getRequiredPermission()
		
		permissionRequester = PermissionRequester(
			this,
			permission
		)
		permissionRequester.onPermissionChecked = {
			updateViewVisibility(
				ContextCompat.checkSelfPermission(
					requireContext(),
					permission
				) == PackageManager.PERMISSION_GRANTED
			)
		}
	}
	
	private fun updateViewVisibility(isPermissionGranted: Boolean) {
		binding.layoutPermissionRequest.isVisible = !isPermissionGranted
		binding.layoutImagesSwipeRefresh.isVisible = isPermissionGranted
		binding.layoutCloseAddContainer.isVisible = isPermissionGranted
		if (isPermissionGranted) viewModel.handleEvent(ImagePickerEvent.PermissionGranted)
	}
	
	private fun checkPermissionsAndUpdateVisibility() {
		val permission = getRequiredPermission()
		updateViewVisibility(
			ContextCompat.checkSelfPermission(
				requireContext(),
				permission
			) == PackageManager.PERMISSION_GRANTED
		)
	}
	
	private fun getRequiredPermission(): String {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			Manifest.permission.READ_MEDIA_IMAGES
		} else {
			Manifest.permission.READ_EXTERNAL_STORAGE
		}
	}
	
	private fun showMaxSelectionDialog() {
		val dialog = AlertDialog.Builder(
			requireContext(),
			R.style.AlertDialog
		).setMessage(R.string.maximum_image_selection_reached)
			.setPositiveButton(android.R.string.ok) { dialog, _ ->
				dialog.dismiss()
			}.create()
		
		dialog.show()
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.default_black))
	}
	
	private fun isLandscape(): Boolean {
		return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
	}
	
}

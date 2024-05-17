package com.opensource.selectsaveimagepicker

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
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
import com.opensource.selectsaveimagepicker.viewmodel.ImagePickerEvent
import com.opensource.selectsaveimagepicker.viewmodel.SelectSaveImagePickerViewModel
import com.opensource.selectsaveimagepicker.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class SelectSaveImagePicker(
	private val config: PickerConfig,
	private val onSelectionComplete: ((List<String>) -> Unit),
) : BottomSheetDialogFragment() {
	
	private var _binding: FragmentSelectSaveImagePickerBinding? = null
	private val binding get() = _binding!!
	
	private val viewModel: SelectSaveImagePickerViewModel by activityViewModels {
		ViewModelFactory(
			maxSelection = config.maxSelection,
			repository = ImageRepository(requireContext())
		)
	}
	
	private lateinit var imagePickerAdapter: ImagePickerAdapter
	private lateinit var permissionRequester: PermissionRequester
	
	private var spanCount: Int = 3
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
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
		if (isLandscape()) {
			spanCount = 5
		} else {
			spanCount = 3
		}
		
		setupPermissionHandling()
		initView()
		initListener()
		initViewModel()
		
	}
	
	override fun onResume() {
		super.onResume()
		checkPermissionsAndUpdateVisibility()
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
	
	private fun initViewModel() {
		lifecycleScope.launch {
			viewModel.images.collect { images ->
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
		binding.tvHandlebarDescription.text = config.descriptionText
		binding.tvAddCount.setTextColor(config.themeColor)
	}
	
	private fun setupImagePickerAdapter() {
		// Initialize the adapter with the configuration
		imagePickerAdapter = ImagePickerAdapter(config) {
			viewModel.handleEvent(ImagePickerEvent.ToggleImage(it))
		}
		
		// Set up RecyclerView layout manager
		val layoutManager = GridLayoutManager(context, spanCount)
		binding.rvImages.layoutManager = layoutManager
		
		// Add item decoration for spacing
		val itemDecoration = GridSpacingItemDecoration(spanCount, config.itemSpacing)
		binding.rvImages.addItemDecoration(itemDecoration)
		
		// Set up the RecyclerView with the adapter
		binding.rvImages.adapter = imagePickerAdapter
		
		// Optimize RecyclerView performance
		binding.rvImages.setItemViewCacheSize(30)
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
			if(config.clearSelectionOnComplete) clearSelectedImages()
			onSelectionComplete(viewModel.selectedImage)
			dismiss()
		}
		
		binding.tvCloseButton.setOnClickListener {
			dismiss()
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
	
	private fun clearSelectedImages() {
		viewModel.clearSelectedImages()
	}
	
}

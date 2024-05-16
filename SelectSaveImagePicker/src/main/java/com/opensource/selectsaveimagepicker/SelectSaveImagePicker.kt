package com.opensource.selectsaveimagepicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class SelectSaveImagePicker : BottomSheetDialogFragment() {
	
	
	private var _binding: FragmentSelectSaveImagePickerBinding? = null
	private val binding get() = _binding!!
	private lateinit var imagePickerAdapter: ImagePickerAdapter
	private lateinit var permissionRequester: PermissionRequester
	private val viewModel: SelectSaveImagePickerViewModel by activityViewModels {
		ViewModelFactory(ImageRepository(requireContext()))
	}
	private val spanCount: Int = 3
	private val spacingPx: Int = 12
	
	override fun onAttach(context: Context) {
		super.onAttach(context)
		context.setTheme(R.style.ImagePickerTheme)
	}
	
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
		
		setupPermissionHandling()
		initView()
		initListener()
		initViewModel()
	}
	
	private fun initViewModel() {
		lifecycleScope.launch {
			viewModel.images.collect { images ->
				Log.d(
					"Picker",
					"image collected ${images.take(10)}}"
				)
				imagePickerAdapter.submitList(images.toList())
			}
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
	}
	
	override fun onResume() {
		super.onResume()        // Recheck permission when fragment resumes
		checkPermissionsAndUpdateVisibility()
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
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}

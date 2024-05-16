package com.opensource.selectsaveimagepicker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.opensource.selectsaveimagepicker.repository.ImageRepository
import com.opensource.selectsaveimagepicker.data.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal sealed class ImagePickerEvent {
	
	data class ToggleImage(
		val image: Image
	) : ImagePickerEvent()
	
	data class LongClickImage(
		val image: Image
	) : ImagePickerEvent()
	
	object ReachedMaxSelection : ImagePickerEvent()
	object PermissionGranted : ImagePickerEvent()
	object ReloadImages : ImagePickerEvent()
}

internal class SelectSaveImagePickerViewModel(
	private val maxSelection: Int = 5,
	private val repository: ImageRepository,
) : ViewModel() {
	
	
	private val _images = MutableStateFlow<List<Image>>(emptyList())
	val images: StateFlow<List<Image>> = _images
	
	private val _event = MutableSharedFlow<ImagePickerEvent>(
		extraBufferCapacity = 10,
		onBufferOverflow = BufferOverflow.DROP_LATEST
	)
	val event: MutableSharedFlow<ImagePickerEvent> = _event
	
	private var _selectedImages = mutableMapOf<String, Int>()
	
	private val _selectedImagesCount = MutableStateFlow(0)
	val selectedImagesCount: StateFlow<Int> = _selectedImagesCount
	
	
	init {
		loadImages()
		Log.d(
			"PickerViewModel",
			"init ViewModel..."
		)
	}
	
	fun handleEvent(event: ImagePickerEvent) {
		when (event) {
			is ImagePickerEvent.LongClickImage -> Unit
			is ImagePickerEvent.ToggleImage -> toggleImageSelection(event.image)
			ImagePickerEvent.PermissionGranted -> permissionGranted()
			else -> Unit
		}
	}
	
	private fun permissionGranted() {
		if (_selectedImagesCount.value != 0) return
		loadImages()
	}
	
	fun loadImages() {
		viewModelScope.launch {
			repository.loadImages().collect { newImages ->
				_images.value = newImages
			}
		}
	}
	
	private fun toggleImageSelection(selectedImage: Image) {
		if (_selectedImagesCount.value >= maxSelection && selectedImage.isSelected.not()) {
			_event.tryEmit(ImagePickerEvent.ReachedMaxSelection)
			return
		}
		val currentImages = _images.value.toMutableList()
		val index = currentImages.indexOfFirst { it.uri == selectedImage.uri }
		if (index != -1) {
			val toggledImage = currentImages[index].copy(
				isSelected = !currentImages[index].isSelected,
				selectionOrder = if (currentImages[index].isSelected) -1 else _selectedImages.size + 1
			)
			
			if (toggledImage.isSelected) {
				_selectedImages[toggledImage.uri] = toggledImage.selectionOrder
			} else {
				_selectedImages.remove(toggledImage.uri)
			}
			
			currentImages[index] = toggledImage
			reorderSelections(currentImages)
			updateSelectedImageCount()
		}
	}
	
	private fun updateSelectedImageCount() {
		_selectedImagesCount.value = _selectedImages.size
	}
	
	private fun reorderSelections(updatedList: MutableList<Image>) {
		var currentSelectionOrder = 1
		val selectedImages = updatedList.filter { it.isSelected }.sortedBy { it.selectionOrder }
		
		selectedImages.forEach { selectedImage ->
			val index = updatedList.indexOfFirst { it.uri == selectedImage.uri }
			if (index != -1) {
				updatedList[index] = selectedImage.copy(selectionOrder = currentSelectionOrder++)
			}
		}
		_images.value = updatedList
	}
}

class ViewModelFactory(
	private val maxSelection: Int,
	private val repository: ImageRepository
) : ViewModelProvider.Factory {
	
	
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(SelectSaveImagePickerViewModel::class.java)) {
			Log.d(
				"PickerViewModel",
				"Creating ViewModel..."
			)
			@Suppress("UNCHECKED_CAST") return SelectSaveImagePickerViewModel(
				maxSelection = maxSelection,
				repository = repository
			) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
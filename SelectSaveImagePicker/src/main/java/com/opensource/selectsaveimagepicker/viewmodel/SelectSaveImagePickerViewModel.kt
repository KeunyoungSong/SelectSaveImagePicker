package com.opensource.selectsaveimagepicker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.opensource.selectsaveimagepicker.repository.ImageRepository
import com.opensource.selectsaveimagepicker.data.Image
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal sealed class ImagePickerEvent {
	
	data class ToggleImage(
		val image: Image
	) : ImagePickerEvent()
	
	data class LongClickImage(
		val image: Image
	) : ImagePickerEvent()
}

internal class SelectSaveImagePickerViewModel(
	private val repository: ImageRepository,
) : ViewModel() {
	
	
	private val _images = MutableStateFlow<List<Image>>(emptyList())
	val images: StateFlow<List<Image>> = _images
	
	private var _selectedImages = mutableMapOf<String, Int>()
	
	private val _event = MutableSharedFlow<Image>()
	val event: MutableSharedFlow<Image> = _event
	
	init {
		Log.d(
			"PickerViewModel",
			"Initializing ViewModel..."
		)
		loadImages()
	}
	
	override fun onCleared() {
		super.onCleared()
		Log.d(
			"PickerViewModel",
			"ViewModel onCleared called"
		)
	}
	
	fun handleEvent(event: ImagePickerEvent) {
		when (event) {
			is ImagePickerEvent.LongClickImage -> Unit
			is ImagePickerEvent.ToggleImage -> toggleImageSelection(event.image)
		}
	}
	
	fun loadImages() {
		viewModelScope.launch {
			repository.loadImages().collect {
				_images.value = it
			}
		}
	}
	
	private fun toggleImageSelection(selectedImage: Image) {
		Log.d(
			"Toggle",
			"SelectedImage: $selectedImage"
		)
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
		}
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

class ViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
	
	
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(SelectSaveImagePickerViewModel::class.java)) {
			Log.d(
				"PickerViewModel",
				"Creating ViewModel..."
			)
			@Suppress("UNCHECKED_CAST") return SelectSaveImagePickerViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
package com.opensource.selectsaveimagepicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.opensource.selectsaveimagepicker.repository.ImageRepository
import com.opensource.selectsaveimagepicker.data.Image
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SelectSaveImagePickerViewModel(
	private val repository: ImageRepository
) : ViewModel() {
	
	private val _images = MutableStateFlow<List<Image>>(emptyList())
	val images: StateFlow<List<Image>> = _images
	private val preselectedImages: List<String>? = null
	
	init {
		loadImages()
	}
	
	private fun loadImages() {
		viewModelScope.launch {
			repository.loadImages(preselectedImages).collect {
				_images.value = it
			}
		}
	}
	
	fun toggleImageSelection(image: Image) {
		val index = _images.value.indexOfFirst { it.uri == image.uri }
		if (index != -1) {
			_images.value = _images.value.toMutableList().apply {
				this[index] = this[index].copy(isSelected = !this[index].isSelected)
			}
		}
	}
	
}

class ViewModelFactory(private val repository: ImageRepository) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(SelectSaveImagePickerViewModel::class.java)) {
			@Suppress("UNCHECKED_CAST")
			return SelectSaveImagePickerViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}
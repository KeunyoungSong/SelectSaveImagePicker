package com.opensource.selectsaveimagepicker.data

data class Image(
	val uri: String,
	val isSelected: Boolean = false,
	val selectionOrder: Int = -1
)


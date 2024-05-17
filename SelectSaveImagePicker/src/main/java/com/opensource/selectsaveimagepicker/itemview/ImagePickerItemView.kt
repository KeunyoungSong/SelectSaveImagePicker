package com.opensource.selectsaveimagepicker.itemview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.opensource.selectsaveimagepicker.R
import com.opensource.selectsaveimagepicker.databinding.ImagePickerItemViewBinding

class ImagePickerItemView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(
	context,
	attrs,
	defStyleAttr
) {
	
	private var binding: ImagePickerItemViewBinding = ImagePickerItemViewBinding.inflate(
		LayoutInflater.from(context),
		this,
		true
	)
	
	private var isSelected: Boolean = false
	private var indicatorNumber: Int = -1
	private var indicatorNumberColor: Int = Color.WHITE
	private var itemStrokeSize: Int = context.resources.getDimensionPixelSize(R.dimen.item_stroke_size)
	private var themeColor: Int = ContextCompat.getColor(context, R.color.themeColor)
	
	init {
		updateSelectionState()
		updateIndicator()
		updateThemeColor()
		updateItemStroke()
	}
	
	fun setItemStrokeWidth(itemStrokeSize: Int) {
		this.itemStrokeSize = itemStrokeSize
		updateItemStroke()
	}
	
	fun setIndicatorNumber(indicatorNumber: Int) {
		this.indicatorNumber = indicatorNumber
		updateIndicator()
	}
	
	fun setIndicatorNumberColor(indicatorNumberColor: Int) {
		this.indicatorNumberColor = indicatorNumberColor
		updateIndicator()
	}
	
	fun setThemeColor(selectionColor: Int) {
		this.themeColor = selectionColor
		updateThemeColor()
	}
	
	override fun setSelected(isSelected: Boolean) {
		if (this.isSelected != isSelected) {
			this.isSelected = isSelected
			updateSelectionState()
			updateIndicator()
			updateItemStroke()
		}
	}
	
	private fun updateSelectionState() {
		binding.vBackground.setBackgroundColor(if (isSelected) themeColor else Color.TRANSPARENT)
	}
	
	private fun updateIndicator() {
		binding.tvIndicator.text = if (isSelected) indicatorNumber.toString() else ""
		binding.tvIndicator.setTextColor(indicatorNumberColor)
		binding.tvIndicator.setBackgroundResource(if (isSelected) R.drawable.ic_selected else R.drawable.ic_unselected)
		binding.tvIndicator.backgroundTintList = if (isSelected) ColorStateList.valueOf(themeColor) else null
	}
	
	private fun updateThemeColor() {
		if (isSelected) {
			binding.vBackground.setBackgroundColor(themeColor)
			binding.tvIndicator.backgroundTintList = ColorStateList.valueOf(themeColor)
		}
	}
	
	private fun updateItemStroke() {
		binding.ivImage.setPadding(if (isSelected) itemStrokeSize else 0)
	}
	
	fun loadImage(url: String) {
		Glide.with(context).load(url).sizeMultiplier(0.6f).centerCrop().into(binding.ivImage)
	}
}

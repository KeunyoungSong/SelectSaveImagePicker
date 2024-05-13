package com.opensource.selectsaveimagepicker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.opensource.selectsaveimagepicker.databinding.ItemImagePickerBinding

class ImagePickerItemView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(
	context,
	attrs,
	defStyleAttr
) {
	
	private var binding: ItemImagePickerBinding = ItemImagePickerBinding.inflate(
		LayoutInflater.from(context),
		this,
		true
	)
	
	private var isSelected: Boolean = false
	private var indicatorNumber: Int = 0
	private var indicatorNumberColor: Int = Color.BLACK
	private var itemStrokeSize: Int = context.resources.getDimensionPixelSize(R.dimen.item_stroke_size)
	private var selectionColor: Int = ContextCompat.getColor(
		context,
		R.color.default_item_image_picker_stroke
	)
	
	init {
		context.theme.obtainStyledAttributes(
			attrs,
			R.styleable.ImagePickerItemView,
			0,
			0
		).apply {
			try {
				isSelected = getBoolean(
					R.styleable.ImagePickerItemView_isSelected,
					isSelected
				)
				indicatorNumber = getInt(
					R.styleable.ImagePickerItemView_indicatorNumber,
					indicatorNumber
				)
				indicatorNumberColor = getColor(
					R.styleable.ImagePickerItemView_indicatorNumberColor,
					indicatorNumberColor
				)
				itemStrokeSize = getInt(
					R.styleable.ImagePickerItemView_itemStrokeSize,
					itemStrokeSize
				)
				selectionColor = getColor(
					R.styleable.ImagePickerItemView_selectionColor,
					selectionColor
				)
			} finally {
				recycle()
			}
			updateView()
		}
	}
	
	fun setItemStrokeSize(itemStrokeSize: Int) {
		this.itemStrokeSize = itemStrokeSize.toPx()
		updateView()
	}
	
	fun setIndicatorNumber(indicatorNumber: Int) {
		this.indicatorNumber = indicatorNumber
		updateView()
	}
	
	fun setIndicatorNumberColor(indicatorNumberColor: Int) {
		this.indicatorNumberColor = indicatorNumberColor
		updateView()
	}
	
	fun setSelectionColor(selectionColor: Int) {
		this.selectionColor = selectionColor
		updateView()
	}
	
	fun setItemStrokeSizeDp(strokeSizeDp: Int) {
		itemStrokeSize = strokeSizeDp
		updateView()
	}
	
	override fun setSelected(isSelected: Boolean) {
		if (this.isSelected != isSelected) {
			this.isSelected = isSelected
			updateView()
		}
	}
	
	private fun updateView() {
		// 텍스트 뷰 업데이트
		binding.tvSelectionIndicator.text = if (isSelected) indicatorNumber.toString() else ""
		binding.tvSelectionIndicator.setTextColor(indicatorNumberColor)
		binding.tvSelectionIndicator.setBackgroundResource(if (isSelected) R.drawable.ic_selected else R.drawable.ic_unselected)
		
		// 배경 및 틴트 색상 설정
		binding.vBackground.setBackgroundColor(selectionColor)
		binding.tvSelectionIndicator.backgroundTintList =
			if (isSelected) ColorStateList.valueOf(selectionColor) else null
		
		// 이미지 뷰 패딩 설정 (테두리 크기를 패딩으로 사용)
		binding.ivImage.setPadding(if (isSelected) itemStrokeSize else 0)
	}
	
	fun loadImage(url: String) {
		Glide.with(context).load(url).into(binding.ivImage)
	}
	
	private fun Int.toPx(): Int {
		return (this * context.resources.displayMetrics.density + 0.5f).toInt()
	}
}



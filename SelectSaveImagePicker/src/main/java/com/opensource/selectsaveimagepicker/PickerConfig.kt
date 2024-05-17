package com.opensource.selectsaveimagepicker

import android.content.Context
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickerConfig(
	val itemSpacing: Int,
	@ColorInt val indicatorNumberColor: Int,
	val itemStrokeWidth: Int,
	@ColorInt val themeColor: Int,
	val descriptionText: String,
	val maxSelection: Int,
	val clearSelectionOnComplete: Boolean,
	val itemViewCacheSize: Int,
	val thumbnailScale: Float
) : Parcelable {
	class Builder(context: Context) {
		private var itemSpacing: Int = context.resources.getDimensionPixelSize(R.dimen.item_spacing)
		private var indicatorNumberColor: Int = ContextCompat.getColor(context, R.color.indicator_text)
		private var itemStrokeWidth: Int = 6
		private var themeColor: Int = ContextCompat.getColor(context, R.color.themeColor)
		private var descriptionText: String = context.resources.getString(R.string.description_bottom_sheet)
		private var maxSelection: Int = 5
		private var clearSelectionOnComplete: Boolean = false
		private var itemViewCacheSize: Int = 30
		private var thumbnailScale: Float = 0.5f
		
		fun setItemSpacing(itemSpacing: Int) = apply { this.itemSpacing = itemSpacing }
		fun setIndicatorNumberColor(@ColorInt indicatorNumberColor: Int) = apply { this.indicatorNumberColor = indicatorNumberColor }
		fun setIndicatorNumberColorHex(colorString: String) = apply { this.indicatorNumberColor = parseColor(colorString) }
		fun setItemStrokeWidth(itemStrokeWidth: Int) = apply { this.itemStrokeWidth = itemStrokeWidth }
		fun setThemeColor(@ColorInt themeColor: Int) = apply { this.themeColor = themeColor }
		fun setThemeColorHex(colorString: String) = apply { this.themeColor = parseColor(colorString) }
		fun setDescriptionText(descriptionText: String) = apply { this.descriptionText = descriptionText }
		fun setMaxSelection(maxSelection: Int) = apply { this.maxSelection = maxSelection }
		fun setClearSelectionOnComplete(clearSelectionOnComplete: Boolean) = apply { this.clearSelectionOnComplete = clearSelectionOnComplete }
		fun setItemViewCacheSize(itemViewCacheSize: Int) = apply { this.itemViewCacheSize = itemViewCacheSize }
		fun setThumbnailScale(thumbnailScale: Float) = apply { this.thumbnailScale = thumbnailScale }
		
		fun build(): PickerConfig {
			return PickerConfig(
				itemSpacing = itemSpacing,
				indicatorNumberColor = indicatorNumberColor,
				itemStrokeWidth = itemStrokeWidth,
				themeColor = themeColor,
				descriptionText = descriptionText,
				maxSelection = maxSelection,
				clearSelectionOnComplete = clearSelectionOnComplete,
				itemViewCacheSize = itemViewCacheSize,
				thumbnailScale = thumbnailScale
			)
		}
		
		private fun parseColor(colorString: String): Int {
			return android.graphics.Color.parseColor(colorString)
		}
	}
}

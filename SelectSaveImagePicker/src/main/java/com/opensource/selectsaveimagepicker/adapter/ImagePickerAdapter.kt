package com.opensource.selectsaveimagepicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.opensource.selectsaveimagepicker.PickerConfig
import com.opensource.selectsaveimagepicker.data.Image
import com.opensource.selectsaveimagepicker.databinding.ItemImageBinding

class ImagePickerAdapter(
	private val pickerConfig: PickerConfig,
	private val onImageSelected: (Image) -> Unit
) : ListAdapter<Image, ImagePickerAdapter.ImageViewHolder>(DiffCallback()) {
	
	inner class ImageViewHolder(private val binding: ItemImageBinding) :
		RecyclerView.ViewHolder(binding.root) {
		
		fun bind(image: Image) {
			binding.imagePickerItem.setOnClickListener {
				onImageSelected(image)
			}
			binding.imagePickerItem.apply {
				isSelected = image.isSelected
				setIndicatorNumber(image.selectionOrder)
				setIndicatorNumberColor(pickerConfig.indicatorNumberColor)
				setThemeColor(pickerConfig.themeColor)
				setItemStrokeWidth(pickerConfig.itemStrokeWidth)
				loadImage(image.uri)
				setThumbnailScale(pickerConfig.thumbnailScale)
			}
		}
	}
	
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ImageViewHolder {
		return ImageViewHolder(
			ItemImageBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}
	
	override fun onBindViewHolder(
		holder: ImageViewHolder,
		position: Int
	) {
		holder.bind(getItem(position))
	}
	
	class DiffCallback : DiffUtil.ItemCallback<Image>() {
		
		override fun areItemsTheSame(
			oldItem: Image,
			newItem: Image
		): Boolean = oldItem.uri == newItem.uri
		
		override fun areContentsTheSame(
			oldItem: Image,
			newItem: Image
		): Boolean =
			oldItem.isSelected == newItem.isSelected && oldItem.selectionOrder == newItem.selectionOrder
	}
}

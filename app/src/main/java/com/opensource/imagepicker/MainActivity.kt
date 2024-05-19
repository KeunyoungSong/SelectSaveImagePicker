package com.opensource.imagepicker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.opensource.imagepicker.databinding.ActivityMainBinding
import com.opensource.selectsaveimagepicker.PickerConfig
import com.opensource.selectsaveimagepicker.SelectSaveImagePicker

class MainActivity : AppCompatActivity(), SelectSaveImagePicker.OnSelectionCompleteListener {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.button.setOnClickListener {
			val customPickerConfig = PickerConfig.Builder(this)
				.setItemSpacing(12)
				.setIndicatorNumberColorHex("#FFFFFF")
				.setItemStrokeWidth(4)
				.setMaxSelection(20)
				.setThemeColor(ContextCompat.getColor(this, R.color.black))
				.setDescriptionText("Please select images")
				.setClearSelectionOnComplete(true)
				.setItemViewCacheSize(30)
				.setThumbnailScale(0.8f)
				.build()
			
			val imagePicker = SelectSaveImagePicker.newInstance(customPickerConfig)
			imagePicker.show(supportFragmentManager, "PICKER")
		}
	}
	
	override fun onSelectionComplete(selectedImages: List<String>) {
		Log.d("Picker", "Selected images: $selectedImages")
		// 선택된 이미지를 처리하는 로직을 여기에 추가합니다.
	}
}

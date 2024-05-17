package com.opensource.imagepicker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.opensource.imagepicker.databinding.ActivityMainBinding
import com.opensource.selectsaveimagepicker.PickerConfig
import com.opensource.selectsaveimagepicker.R.color
import com.opensource.selectsaveimagepicker.SelectSaveImagePicker

class MainActivity : AppCompatActivity(), SelectSaveImagePicker.OnSelectionCompleteListener {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.button.setOnClickListener {
			val customPickerConfig = PickerConfig.Builder(this)
				.setItemSpacing(16) // 16 픽셀 간격
				.setIndicatorNumberColorHex("#FFFFFF") // 흰색 인디케이터 번호 (HEX)
				.setItemStrokeWidth(4) // 4 픽셀 스트로크 너비
				.setThemeColor(ContextCompat.getColor(this, color.themeColor))
				.setDescriptionText("Please select images") // 사용자 정의 설명 텍스트
				.setClearSelectionOnComplete(true)
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

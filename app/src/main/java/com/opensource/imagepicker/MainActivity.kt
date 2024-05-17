package com.opensource.imagepicker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.opensource.imagepicker.databinding.ActivityMainBinding
import com.opensource.selectsaveimagepicker.PickerConfig
import com.opensource.selectsaveimagepicker.R.*
import com.opensource.selectsaveimagepicker.SelectSaveImagePicker

class MainActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.button.setOnClickListener {
			val customPickerConfig = PickerConfig.Builder(this)
				.setItemSpacing(16) // 16 픽셀 간격
				.setIndicatorNumberColorHex("#FFFFFF") // 흰색 인디케이터 번호 (HEX)
				.setItemStrokeWidth(4) // 4 픽셀 스트로크 너비
				.setThemeColor(ContextCompat.getColor(this, color.themeColor))
				//.setThemeColorHex("#0000FF") // 파란색 테마 (HEX)
				.setDescriptionText("Please select images") // 사용자 정의 설명 텍스트
				.setClearSelectionOnComplete(true)
				.build()
			val imagePicker = SelectSaveImagePicker(customPickerConfig){
				Log.d("Return", "$it")
			}
			imagePicker.show(supportFragmentManager, "")
		}
		
		
	}
}
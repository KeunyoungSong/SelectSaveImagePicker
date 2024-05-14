package com.opensource.imagepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.opensource.imagepicker.databinding.ActivityMainBinding
import com.opensource.selectsaveimagepicker.SelectSaveImagePicker

class
MainActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityMainBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		binding.button.setOnClickListener {
			val dialog = SelectSaveImagePicker()
			dialog.show(supportFragmentManager, "")
		}
		
		
	}
}
package com.opensource.selectsaveimagepicker.repository

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.opensource.selectsaveimagepicker.data.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ImageRepository(private val context: Context) {
	
	fun loadImages(): Flow<List<Image>> = flow {
		val images = mutableListOf<Image>()
		val projection = arrayOf(MediaStore.Images.Media._ID)
		val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
		
		val cursor = context.contentResolver.query(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			projection,
			null,
			null,
			sortOrder
		)
		
		cursor?.use {
			val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val contentUri: Uri = Uri.withAppendedPath(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					id.toString()
				)
				images.add(
					Image(
						contentUri.toString(),
						false
					)
				)
			}
		}
		emit(images)  // Emit the list of images as a single batch
	}.flowOn(Dispatchers.IO)  // Perform database operations on the I/O dispatcher
}
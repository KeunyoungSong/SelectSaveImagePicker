package com.opensource.selectsaveimagepicker.utils

import android.util.Log

fun logMemoryUsageInKB() {
	val runtime = Runtime.getRuntime()
	val usedMemory = runtime.totalMemory() - runtime.freeMemory()
	Log.d("MemoryUsage", "Used memory: ${usedMemory.toFloat() / (1024)} KB")
}

fun logMemoryUsageInMB() {
	val runtime = Runtime.getRuntime()
	val usedMemory = runtime.totalMemory() - runtime.freeMemory()
		Log.d("MemoryUsage", "Used memory: ${usedMemory.toFloat() / (1024 * 1024)} MB")
}
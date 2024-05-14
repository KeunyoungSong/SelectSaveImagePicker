package com.opensource.selectsaveimagepicker.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
	private val spanCount: Int,
	private val spacing: Int,
) : RecyclerView.ItemDecoration() {
	
	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		val position = parent.getChildAdapterPosition(view) // 아이템 위치
		val column = position % spanCount // 현재 위치의 열 인덱스
		
		outRect.left = column * spacing / spanCount
		outRect.right = spacing - (column + 1) * spacing / spanCount
		
		if (position >= spanCount) {
			outRect.top = spacing
		}
	}
}

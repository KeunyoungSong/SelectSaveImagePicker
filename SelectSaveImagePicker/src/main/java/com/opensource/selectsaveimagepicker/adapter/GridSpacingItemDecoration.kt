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
		val itemCount = state.itemCount // 아이템 총 개수
		val extraBottomSpacing = 500 // 마지막 줄 바닥 여백 추가
		
		outRect.left = column * spacing / spanCount
		outRect.right = spacing - (column + 1) * spacing / spanCount
		
		if (position >= spanCount) {
			outRect.top = spacing
		} // 마지막 줄의 경우에만 추가 바닥 여백 적용
		if (position == itemCount) {
			outRect.bottom = extraBottomSpacing
		}
	}
}

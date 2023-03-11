package com.example.skillcinema.presentation.adapters

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.dpToPixel

class BasicVerticalLinearDivider(val context: Context): RecyclerView.ItemDecoration() {
    private val space = context.dpToPixel(8f).toInt()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
            .let { if (it == RecyclerView.NO_POSITION) return else it }
        outRect.bottom = space
        outRect.top = if (position == 0) (space * 3.25).toInt() else 0
    }
}
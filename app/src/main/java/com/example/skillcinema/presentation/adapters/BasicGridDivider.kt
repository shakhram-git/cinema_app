package com.example.skillcinema.presentation.adapters

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.dpToPixel

class BasicGridDivider(val context: Context, private val spanCount: Int) :
    RecyclerView.ItemDecoration() {
    private val space = context.dpToPixel(8f).toInt()
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
            .let { if (it == RecyclerView.NO_POSITION) return else it }
        outRect.left = if (position < spanCount) (space * 3.25).toInt() else 0
        outRect.right = space
        outRect.bottom = space



    }
}
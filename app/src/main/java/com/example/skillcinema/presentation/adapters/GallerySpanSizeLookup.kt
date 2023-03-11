package com.example.skillcinema.presentation.adapters

import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup

class GallerySpanSizeLookup(
    private val span1: Int,
    private val span2: Int,
    private val spanPos: Int
) : SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return if (position % spanPos == 0) span2 else span1
    }
}
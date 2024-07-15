package dev.maruffirdaus.stories.ui.recyclerview.itemdecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.maruffirdaus.stories.R

class DividerItemDecoration(context: Context, resId: Int) : RecyclerView.ItemDecoration() {
    private val divider = ContextCompat.getDrawable(context, resId)
    private val horizontalMargin = context.resources.getDimensionPixelSize(R.dimen.dimen_16dp)
    private val dividerHeight = context.resources.getDimensionPixelSize(R.dimen.dimen_1dp)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.bottom = dividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft + horizontalMargin
        val right = parent.width - parent.paddingRight - horizontalMargin
        val childCount = parent.childCount

        for (i in 0..childCount - 2) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight

            divider?.setBounds(left, top, right, bottom)
            divider?.draw(c)
        }
    }
}
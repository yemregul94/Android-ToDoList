package com.moonlight.todolist.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.moonlight.todolist.R
import kotlin.math.abs

class SwipeDecorator {
    fun decorateSwiper(itemView: View, c:Canvas, dX:Float, recyclerView: RecyclerView){
        val context = recyclerView.context

        val backgroundCornerOffset = c.width/16
        val absoluteDisplacement = abs(dX).toInt()
        val iconMargin = 16
        val background = ColorDrawable(Color.TRANSPARENT)
        val p = Paint()

        if (dX > 0) { // Swiping to the right

            background.setBounds(itemView.left +dX.toInt() + backgroundCornerOffset, itemView.top, itemView.left, itemView.bottom)
            background.draw(c)

            val icon = ActivityCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)
            val iconTop = (itemView.height / 2) - (icon!!.intrinsicHeight / 2) + itemView.top
            icon.setBounds(absoluteDisplacement - icon.intrinsicWidth, iconTop , absoluteDisplacement, iconTop + icon.intrinsicHeight)
            background.color = context.resources.getColor(R.color.red)
            background.draw(c)
            icon.draw(c)

            val text = context.getString(R.string.delete)
            val textSize = 60f
            p.color = Color.WHITE
            p.isAntiAlias = true
            p.textSize = textSize

            val textWidth = p.measureText(text)

            c.drawText(text, icon.bounds.left - iconMargin - textWidth, icon.bounds.bottom - 10f, p)


        } else if (dX < 0) { // Swiping to the left

            background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset, itemView.top, itemView.right, itemView.bottom)
            background.draw(c)

            val icon = ActivityCompat.getDrawable(recyclerView.context, R.drawable.ic_check2)
            val iconTop = (itemView.height / 2) - (icon!!.intrinsicHeight / 2) + itemView.top
            icon.setBounds(itemView.width - absoluteDisplacement, iconTop , itemView.width + icon.intrinsicWidth - absoluteDisplacement, iconTop + icon.intrinsicHeight)
            background.color = context.resources.getColor(R.color.blue)

            background.draw(c)
            icon.draw(c)


            val text = context.getString(R.string.complete)
            val textSize = 60f
            p.color = Color.WHITE
            p.isAntiAlias = true
            p.textSize = textSize

            c.drawText(text, icon.bounds.right.toFloat() + iconMargin, icon.bounds.bottom - 10f, p)
        }

    }
}
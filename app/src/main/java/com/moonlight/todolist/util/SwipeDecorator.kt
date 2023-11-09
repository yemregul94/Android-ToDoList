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
    fun decorateSwiper(itemView: View, c:Canvas, dX:Float, recyclerView: RecyclerView, archived: Boolean = false){
        val context = recyclerView.context

        //val absoluteDisplacement = abs(dX).toInt()
        val backgroundCornerOffset = c.width/16
        val iconMargin = 16
        val background = ColorDrawable(Color.TRANSPARENT)

        if (dX > 0) { // Swiping to the right

            background.setBounds(itemView.left +dX.toInt() + backgroundCornerOffset, itemView.top, itemView.left, itemView.bottom)
            background.draw(c)

            val icon = ActivityCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)
            val iconTop = (itemView.height / 2) - (icon!!.intrinsicHeight / 2) + itemView.top
            icon.setTint(context.resources.getColor(R.color.white))

            icon.setBounds(iconMargin, iconTop , iconMargin + icon.intrinsicWidth, iconTop + icon.intrinsicHeight)
            background.color = context.resources.getColor(R.color.red)
            background.draw(c)
            icon.draw(c)


        } else if (dX < 0) { // Swiping to the left

            background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset, itemView.top, itemView.right, itemView.bottom)
            background.draw(c)

            var icon = ActivityCompat.getDrawable(recyclerView.context, R.drawable.ic_check2)
            if(archived){
                icon = ActivityCompat.getDrawable(recyclerView.context, R.drawable.ic_archive)
            }
            val iconTop = (itemView.height / 2) - (icon!!.intrinsicHeight / 2) + itemView.top
            icon.setTint(context.resources.getColor(R.color.white))

            icon.setBounds(itemView.width - icon.intrinsicWidth - iconMargin , iconTop , itemView.width - iconMargin, iconTop + icon.intrinsicHeight)
            background.color = context.resources.getColor(R.color.blue)
            background.draw(c)
            icon.draw(c)
        }

    }
}
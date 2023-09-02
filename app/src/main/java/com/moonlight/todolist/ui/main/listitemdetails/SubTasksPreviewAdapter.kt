package com.moonlight.todolist.ui.main.listitemdetails

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoSubTask
import com.moonlight.todolist.databinding.ItemSubTaskPreviewBinding

class SubTasksPreviewAdapter : RecyclerView.Adapter<SubTasksPreviewAdapter.ViewHolder>() {

    var onCompleteClick: ((Int) -> Unit)? = null

    inner class ViewHolder(binding: ItemSubTaskPreviewBinding) : RecyclerView.ViewHolder(binding.root){
        var binding: ItemSubTaskPreviewBinding
        init {
            this.binding = binding
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ToDoSubTask>() {
        override fun areItemsTheSame(oldItem: ToDoSubTask, newItem: ToDoSubTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDoSubTask, newItem: ToDoSubTask): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSubTaskPreviewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sub_task_preview, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val bind = holder.binding
        bind.toDoItem = item

        if(item.completed){
            bind.tvItemTitle.paintFlags = bind.tvItemTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        bind.layoutSubTask.setOnClickListener {
            onCompleteClick?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
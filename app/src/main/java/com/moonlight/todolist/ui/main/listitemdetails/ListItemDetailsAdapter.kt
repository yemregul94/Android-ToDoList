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
import com.moonlight.todolist.databinding.ItemSubTaskBinding

class ListItemDetailsAdapter : RecyclerView.Adapter<ListItemDetailsAdapter.ViewHolder>() {

    var onSubTaskClick: ((Int) -> Unit)? = null
    var onSubTaskCheckClick: ((Int) -> Unit)? = null

    inner class ViewHolder(binding: ItemSubTaskBinding) : RecyclerView.ViewHolder(binding.root){
        var binding: ItemSubTaskBinding
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemDetailsAdapter.ViewHolder {
        val binding: ItemSubTaskBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sub_task, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListItemDetailsAdapter.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val bind = holder.binding
        bind.toDoItem = item

        bind.layoutSubTask.setOnClickListener {
            onSubTaskClick?.invoke(position)
        }

        bind.checkItemComplete.setOnClickListener {
            onSubTaskCheckClick?.invoke(position)
        }

        if(item.completed){
            bind.tvItemTitle.paintFlags = bind.tvItemTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            bind.tvItemTitle.paintFlags = 0
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
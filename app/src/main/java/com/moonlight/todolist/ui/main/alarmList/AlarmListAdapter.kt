package com.moonlight.todolist.ui.main.alarmList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.databinding.ItemAlarmBinding
import com.moonlight.todolist.util.formatTime
import com.moonlight.todolist.util.getTimeInMillis

class AlarmListAdapter : RecyclerView.Adapter<AlarmListAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root){
        var binding: ItemAlarmBinding
        init {
            this.binding = binding
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ToDoListItem>() {
        override fun areItemsTheSame(oldItem: ToDoListItem, newItem: ToDoListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDoListItem, newItem: ToDoListItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAlarmBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_alarm, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val bind = holder.binding
        bind.toDoItem = item
        bind.alarmTime = formatTime(item.alarmTime)

        bind.isExpired = getTimeInMillis(item.alarmTime) < System.currentTimeMillis()

        bind.layoutAlarm.setOnClickListener {
            val nav = AlarmListFragmentDirections.actionAlarmToDetails(item)
            Navigation.findNavController(it).navigate(nav)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
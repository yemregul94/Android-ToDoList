package com.moonlight.todolist.ui.main.dashboard

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListExpand
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.databinding.ItemToDoListItemBinding
import com.moonlight.todolist.ui.main.listitemdetails.SubTasksPreviewAdapter

class DashboardAdapter : RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    var onCompleteClick: ((Int) -> Unit)? = null
    var onFavClick: ((Int) -> Unit)? = null
    var onSubTaskCompleteClick: ((Int, Int) -> Unit)? = null

    private var expandStatusList: MutableList<ToDoListExpand> = mutableListOf()

    inner class ViewHolder(binding: ItemToDoListItemBinding) : RecyclerView.ViewHolder(binding.root){
        var binding: ItemToDoListItemBinding
        init {
            this.binding = binding
            updateExpandStatusList()
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
        val binding: ItemToDoListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_to_do_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = differ.currentList[position]
        val bind = holder.binding
        bind.toDoList = listItem

        if(listItem.completed){
            bind.tvListTitle.paintFlags = bind.tvListTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            bind.tvListTitle.paintFlags = 0
        }

        val colorID = when (listItem.color) {
            "color1" -> R.color.color1
            "color2" -> R.color.color2
            "color3" -> R.color.color3
            "color4" -> R.color.color4
            "color5" -> R.color.color5
            "color6" -> R.color.color6
            else -> R.color.color1
        }
        bind.color = ContextCompat.getColor(bind.root.context, colorID)

        val priorityArray = holder.itemView.context.resources.getStringArray(R.array.priority_array)
        bind.priority = priorityArray[listItem.priority]

        val priorityColor = when (listItem.priority) {
            0 -> R.color.priority_low
            1 -> R.color.priority_medium
            2 -> R.color.priority_high
            3 -> R.color.priority_top
            else -> R.color.priority_medium
        }
        bind.priorityColor = ContextCompat.getColor(bind.root.context, priorityColor)

        bind.taskNull = listItem.subTaskList == null
        bind.expandStatus = expandStatusList.find { it.id == listItem.id }?.expandStatus ?: true
        if(listItem.subTaskList == null){
            bind.expandStatus = false
        }

        bind.layoutSubTask.setOnClickListener {
            val nav = DashboardFragmentDirections.actionGoToListDetails(listItem)
            Navigation.findNavController(it).navigate(nav)
        }

        bind.checkComplete.setOnClickListener {
            onCompleteClick?.invoke(holder.adapterPosition)
        }

        bind.checkFavorite.setOnClickListener {
            onFavClick?.invoke(holder.adapterPosition)
        }

        bind.checkExpand.setOnCheckedChangeListener { _, isChecked ->
            bind.expandStatus = isChecked

            if(expandStatusList.find { it.id == listItem.id } == null){
                expandStatusList.add(ToDoListExpand(listItem.id!!, isChecked))
            }
            else {
                expandStatusList.find { it.id == listItem.id }?.expandStatus = isChecked
            }
        }

        val adapter = SubTasksPreviewAdapter()

        bind.rvItemsPreview.apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(this.context)
            adapter.differ.submitList(listItem.subTaskList)

            adapter.onCompleteClick = {taskPosition ->
                onSubTaskCompleteClick?.invoke(holder.adapterPosition, taskPosition)
            }
        }
    }

    fun updateExpandStatusList(){
        if(expandStatusList.size < itemCount){
            expandStatusList.clear()
            differ.currentList.forEach {
                expandStatusList.add(ToDoListExpand(it.id!!, true))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
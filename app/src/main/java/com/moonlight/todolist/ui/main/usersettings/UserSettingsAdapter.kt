package com.moonlight.todolist.ui.main.usersettings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.moonlight.todolist.R
import com.moonlight.todolist.databinding.ItemCategoryBinding

class UserSettingsAdapter : RecyclerView.Adapter<UserSettingsAdapter.ViewHolder>() {

    var onDeleteClick: ((Int) -> Unit)? = null
    var onItemClick: ((Int) -> Unit)? = null

    inner class ViewHolder(binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        var binding: ItemCategoryBinding
        init {
            this.binding = binding
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemCategoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_category, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val bind = holder.binding
        bind.category = item

        bind.imageDelete.setOnClickListener {
            onDeleteClick?.invoke(holder.adapterPosition)
        }

        bind.layoutCategory.setOnClickListener {
            onItemClick?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
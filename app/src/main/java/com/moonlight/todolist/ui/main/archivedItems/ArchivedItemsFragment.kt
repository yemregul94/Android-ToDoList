package com.moonlight.todolist.ui.main.archivedItems

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.databinding.FragmentArchivedItemsBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import com.moonlight.todolist.ui.main.MainViewModel
import com.moonlight.todolist.util.SwipeDecorator
import com.moonlight.todolist.util.cancelAlarm
import com.moonlight.todolist.util.setAlarm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedItemsFragment : Fragment() {

    private var _binding: FragmentArchivedItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArchivedItemsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adapter = ArchivedItemsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArchivedItemsBinding.inflate(inflater, container, false)

        setAdapter()
        loadArchivedListItems()
        setOnRecyclerViewItemSwipedListener()

        return binding.root
    }

    private fun loadArchivedListItems() {
        mainViewModel.toDoListsItem.observe(viewLifecycleOwner){
            adapter.differ.submitList(it.filter { it.archived })
        }
    }

    private fun setAdapter(){
        binding.rvArchived.apply {
            adapter = this@ArchivedItemsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        adapter.onCompleteClick = { position ->
            toggleComplete(adapter.differ.currentList[position])
        }
        adapter.onSubTaskCompleteClick = { position, taskPosition ->
            toggleSubTaskComplete(adapter.differ.currentList[position], position, taskPosition)
        }
        adapter.onFavClick = { position ->
            toggleFavorite(adapter.differ.currentList[position])
        }
        adapter.onDuplicateClick = { position ->
            duplicateListItem(adapter.differ.currentList[position])
        }
        adapter.onArchiveClick = { position ->
            toggleArchive(adapter.differ.currentList[position])
        }
    }

    private fun duplicateListItem(listItem: ToDoListItem) {
        mainViewModel.duplicateListItem(listItem, getString(R.string.copy))
    }

    private fun deleteListItem(listItem: ToDoListItem){
        mainViewModel.deleteListItems(listItem.id!!)
        cancelAlarm(requireContext(), listItem)
    }

    private fun toggleComplete(listItem: ToDoListItem){
        mainViewModel.toggleComplete(listItem)
    }

    private fun toggleSubTaskComplete(listItem: ToDoListItem, listPosition: Int, taskPosition: Int){
        mainViewModel.toggleSubTaskComplete(listItem, taskPosition)
        adapter.notifyItemChanged(listPosition)
    }

    private fun toggleFavorite(listItem: ToDoListItem){
        mainViewModel.toggleFavorite(listItem)
    }

    private fun toggleArchive(listItem: ToDoListItem){
        mainViewModel.toggleArchive(listItem)
    }

    private fun setOnRecyclerViewItemSwipedListener() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedListItem = adapter.differ.currentList[position]

                if(direction == ItemTouchHelper.LEFT){
                    toggleArchive(swipedListItem)
                }

                else if(direction == ItemTouchHelper.RIGHT){
                    deleteListItem(swipedListItem)

                    Snackbar.make(viewHolder.itemView, getString(R.string.removed, swipedListItem.title), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo)) {
                            mainViewModel.undoDelete(swipedListItem)
                            setAlarm(requireContext(), swipedListItem)
                            binding.rvArchived.postDelayed({ binding.rvArchived.scrollToPosition(position) }, 100)
                        }
                        .show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView

                SwipeDecorator().decorateSwiper(itemView, c, dX, recyclerView, true)

            }

        }).attachToRecyclerView(binding.rvArchived)
    }

}

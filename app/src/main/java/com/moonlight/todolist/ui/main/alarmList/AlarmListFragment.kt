package com.moonlight.todolist.ui.main.alarmList

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
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.databinding.FragmentAlarmListBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import com.moonlight.todolist.ui.main.MainViewModel
import com.moonlight.todolist.util.SwipeDecorator
import com.moonlight.todolist.util.cancelAlarm
import com.moonlight.todolist.util.getTimeInMillis
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlarmListFragment : Fragment() {

    private var _binding: FragmentAlarmListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlarmListViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adapter = AlarmListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlarmListBinding.inflate(inflater, container, false)

        loadLists()
        setAdapter()
        setOnRecyclerViewItemSwipedListener()

        return binding.root
    }

    private fun loadLists() {
        viewModel.setUID(authViewModel.uid.toString())
        viewModel.toDoListsItem.observe(viewLifecycleOwner){
            adapter.differ.submitList(it.filter { !it.alarmTime.isNullOrEmpty() }.sortedBy { getTimeInMillis(it.alarmTime) })
        }
    }

    private fun setAdapter(){
        binding.rvAlarms.apply {
            adapter = this@AlarmListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setOnRecyclerViewItemSwipedListener() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedListItem = adapter.differ.currentList[position]

                if(direction == ItemTouchHelper.RIGHT){
                    removeAlarm(swipedListItem)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView

                SwipeDecorator().decorateSwiper(itemView, c, dX, recyclerView)

            }

        }).attachToRecyclerView(binding.rvAlarms)
    }

    private fun removeAlarm(swipedListItem: ToDoListItem){
        mainViewModel.removeAlarm(swipedListItem)
        cancelAlarm(requireContext(), swipedListItem)
    }
}
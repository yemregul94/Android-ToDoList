package com.moonlight.todolist.ui.main.listitemdetails

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.style
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.model.ToDoSubTask
import com.moonlight.todolist.databinding.FragmentListItemDetailsBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import com.moonlight.todolist.ui.main.MainViewModel
import com.moonlight.todolist.util.CustomAlertDialog
import com.moonlight.todolist.util.SwipeDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Collections
import java.util.Locale

@AndroidEntryPoint
class ListItemDetailsFragment : Fragment() {

    private var _binding: FragmentListItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListItemDetailsViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter = ListItemDetailsAdapter()

    private var toDoSubTasks : MutableList<ToDoSubTask> = mutableListOf()
    private var selectedPriority = 1
    private var selectedColor = ""
    private var selectedCategory = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemDetailsBinding.inflate(inflater, container, false)

        val bundle: ListItemDetailsFragmentArgs by navArgs()
        val listItem = bundle.toDoListItem
        val subTasks = bundle.toDoListItem?.subTaskList

        if(listItem?.id != null){
            binding.btnUpdate.visibility = View.VISIBLE
            binding.btnSave.visibility = View.INVISIBLE
            selectedPriority = listItem.priority
            binding.updateTime = formatTime(listItem.updateTime)
            selectedColor = listItem.color
            selectedCategory = listItem.category
        }
        else {
            binding.btnSave.isEnabled = false
            binding.txtTitle.error = getString(R.string.alert_empty_title)
        }

        if(subTasks != null){
            toDoSubTasks = subTasks.toMutableList()
        }

        binding.apply {
            toDoListItem = listItem
            checkStatus = listItem?.completed

            btnSave.setOnClickListener { saveListItem() }

            btnUpdate.setOnClickListener { updateListItem(bundle) }

            btnReturn.setOnClickListener { returnToDashboard() }

            imageNewSubTask.setOnClickListener { newSubTask() }

            checkCompleteDetails.setOnClickListener { binding.checkStatus = binding.checkCompleteDetails.isChecked }

            chipCategoryAdd.setOnClickListener { newCategory() }

            imageSettings.setOnClickListener {
                val nav = ListItemDetailsFragmentDirections.actionGoToCategorySettings()
                Navigation.findNavController(requireView()).navigate(nav)
            }

            txtTitle.addTextChangedListener {
                if(it.toString().isEmpty()){
                    binding.btnSave.isEnabled = false
                    binding.btnUpdate.isEnabled = false
                    layoutTitle.isErrorEnabled = true
                    layoutTitle.error = getString(R.string.alert_empty_title)
                }
                else {
                    binding.btnSave.isEnabled = true
                    binding.btnUpdate.isEnabled = true
                    layoutTitle.isErrorEnabled = false
                    layoutTitle.error = null
                }
            }
        }

        setAdapter()
        setPriority()
        setColor()
        loadCategories()
        observeCategoryChips()
        setOnRecyclerViewItemSwipedListener()

        return binding.root
    }

    private fun newCategory() {
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                val userDatas: ArrayList<String> = arrayListOf()
                userDatas.addAll(mainViewModel.user.value!!.categoryList)
                userDatas.add(settingTextFirst)
                mainViewModel.createUserData(mainViewModel.user.value!!.copy(categoryList = userDatas), authViewModel.uid)
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "category",null, null, positiveButtonClickListener)
    }

    private fun returnToDashboard(){
        requireActivity().onBackPressed()
    }

    private fun loadCategories() {

        mainViewModel.user.observe(viewLifecycleOwner){
            if (it!=null){
                if(binding.chipsCategory.childCount > 1){
                    binding.chipsCategory.removeViews(1, it.categoryList.size)
                }

                it.categoryList.forEach { categoryName ->
                    createCategoryChip(categoryName)
                }

                createCategoryChip(getString(R.string.other))

                var checkID = mainViewModel.user.value?.categoryList?.indexOf(selectedCategory)
                if(checkID == -1){
                    checkID = mainViewModel.user.value?.categoryList?.size
                }

                if(binding.chipsCategory.childCount > 1){
                    binding.chipsCategory.check(binding.chipsCategory[checkID!!+1].id)
                }
            }
        }

    }

    private fun createCategoryChip(text: String){
        val chip = Chip(requireContext())
        val chipDrawable = ChipDrawable.createFromAttributes(requireContext(), null, 0, style.Widget_MaterialComponents_Chip_Filter)
        chip.setChipDrawable(chipDrawable)
        chip.setCheckedIconTintResource(R.color.main_text)

        chip.chipStrokeWidth = 3f
        chip.ensureAccessibleTouchTarget(0)

        chip.text = text
        binding.chipsCategory.addView(chip)
    }

    private fun observeCategoryChips() {
        binding.chipsCategory.setOnCheckedStateChangeListener { group, _ ->
            group.forEachIndexed { index, it -> it as Chip
                it.setChipStrokeColorResource(R.color.theme_primary_variant)
                if(it.isChecked){
                    it.setChipBackgroundColorResource(R.color.selected_chip_background)
                    it.setTextColor(resources.getColor(R.color.selected_chip_text))
                    selectedCategory = it.text.toString()
                }
                else if(!it.isChecked && index!=0) {
                    it.setChipBackgroundColorResource(R.color.not_selected_chip_background)
                    it.setTextColor(resources.getColor(R.color.not_selected_chip_text))
                }
            }
        }
    }

    private fun formatTime(time: String) : String{
        return viewModel.formatTime(time)
    }

    private fun setAdapter(){
        adapter.differ.submitList(toDoSubTasks)

        binding.rvSubTasks.apply {
            adapter = this@ListItemDetailsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        adapter.onSubTaskClick = { position -> editSubTask(position) }

        adapter.onSubTaskCheckClick = { position -> toggleComplete(position) }
    }

    private fun saveListItem(){
        val newListItem = ToDoListItem("",
            binding.txtTitle.text.toString(),
            binding.txtDesc.text.toString(),
            selectedPriority,
            binding.checkCompleteDetails.isChecked,
            selectedCategory,
            toDoSubTasks,
            selectedColor,
            binding.checkFavoriteDetails.isChecked,
            archived = false,
            Calendar.getInstance(Locale.ENGLISH).time.toString(),
            Calendar.getInstance(Locale.ENGLISH).time.toString()
        )
        newListItem(newListItem, authViewModel.uid)
        Toast.makeText(requireContext(), getString(R.string.list_item_created, binding.txtTitle.text.toString()), Toast.LENGTH_LONG).show()

        returnToDashboard()
    }

    private fun updateListItem(bundle: ListItemDetailsFragmentArgs){
        val newListItem = ToDoListItem(bundle.toDoListItem!!.id,
            binding.txtTitle.text.toString(),
            binding.txtDesc.text.toString(),
            selectedPriority,
            binding.checkCompleteDetails.isChecked,
            selectedCategory,
            toDoSubTasks,
            selectedColor,
            binding.checkFavoriteDetails.isChecked,
            bundle.toDoListItem!!.archived,
            bundle.toDoListItem!!.createTime,
            Calendar.getInstance(Locale.ENGLISH).time.toString()
        )
        updateListItem(newListItem, authViewModel.uid)
        Toast.makeText(requireContext(), getString(R.string.list_item_updated, binding.txtTitle.text.toString()), Toast.LENGTH_LONG).show()

        returnToDashboard()
    }

    private fun setPriority(){

        binding.apply {
            tvPriorityLow.setOnClickListener { sbPriority.progress = 0 }
            tvPriorityMedium.setOnClickListener { sbPriority.progress = 1 }
            tvPriorityHigh.setOnClickListener { sbPriority.progress = 2 }
            tvPriorityTop.setOnClickListener { sbPriority.progress = 3 }

            sbPriority.progress = selectedPriority

            sbPriority.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) { selectedPriority = progress }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setColor(){

        if(selectedColor.isEmpty()){
            selectedColor = "color1"
        }

        when(selectedColor){
            "color1" -> binding.chipsColors.check(binding.chipsColors[0].id)
            "color2" -> binding.chipsColors.check(binding.chipsColors[1].id)
            "color3" -> binding.chipsColors.check(binding.chipsColors[2].id)
            "color4" -> binding.chipsColors.check(binding.chipsColors[3].id)
            "color5" -> binding.chipsColors.check(binding.chipsColors[4].id)
            "color6" -> binding.chipsColors.check(binding.chipsColors[5].id)
            else -> binding.chipsColors.check(binding.chipsColors[0].id)
        }

        binding.chipsColors.setOnCheckedStateChangeListener { group, checkedIds ->
            checkedIds.forEach {
                selectedColor = "color${group.indexOfChild(group.findViewById(it))+1}"
            }
        }
    }

    private fun newListItem(listItem: ToDoListItem, uid: String?){
        viewModel.newListItem(listItem, uid)
    }

    private fun updateListItem(listItem: ToDoListItem, uid: String?){
        viewModel.updateListItem(listItem, uid)
    }

    private fun newSubTask(){
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                if(!toDoSubTasks.contains(ToDoSubTask(title = settingTextFirst))) {
                    toDoSubTasks.add(ToDoSubTask("", settingTextFirst, check))
                    adapter.notifyItemInserted(adapter.itemCount)
                }
                else {
                    Toast.makeText(requireContext(), getString(R.string.already_exists, settingTextFirst), Toast.LENGTH_SHORT).show()
                }
            }
        }

        CustomAlertDialog().showCustomAlert(requireActivity(), "subTask", null, null, positiveButtonClickListener)
    }

    private fun editSubTask(position: Int){

        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                if(!toDoSubTasks.contains(ToDoSubTask(title = settingTextFirst))) {
                    toDoSubTasks[position] = ToDoSubTask("",settingTextFirst, check)
                    adapter.notifyItemChanged(position)
                }
                else {
                    Toast.makeText(requireContext(), getString(R.string.already_exists, settingTextFirst), Toast.LENGTH_SHORT).show()
                }
            }
        }

        CustomAlertDialog().showCustomAlert(requireActivity(), "subTask", toDoSubTasks[position].title, toDoSubTasks[position].completed, positiveButtonClickListener)
    }

    private fun deleteSubTask(position: Int){
        if(toDoSubTasks.isNotEmpty()){
            toDoSubTasks.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

    private fun undoDelete(position: Int, subTask: ToDoSubTask){
        toDoSubTasks.add(position, subTask)
        adapter.notifyItemInserted(position)
    }

    private fun toggleComplete(position: Int){
        toDoSubTasks[position] = toDoSubTasks[position].copy(completed = !toDoSubTasks[position].completed)
        adapter.notifyItemChanged(position)
    }

    private fun setOnRecyclerViewItemSwipedListener() {
        ItemTouchHelper(object : SimpleCallback(UP or DOWN, LEFT or RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val posOrigin = viewHolder.adapterPosition
                val posTarget = target.adapterPosition

                Collections.swap(toDoSubTasks, posOrigin, posTarget)
                adapter.notifyItemMoved(posOrigin, posTarget)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if(direction == LEFT){
                    toggleComplete(position)
                }
                else if(direction == RIGHT){
                    val deletedSubTask = toDoSubTasks[position]
                    deleteSubTask(position)

                    Snackbar.make(viewHolder.itemView, getString(R.string.removed, deletedSubTask.title), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo)) {
                            undoDelete(position, deletedSubTask)
                            binding.rvSubTasks.postDelayed({ binding.rvSubTasks.scrollToPosition(position) }, 100)
                        }
                        .show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView

                SwipeDecorator().decorateSwiper(itemView, c, dX, recyclerView)

            }

        }).attachToRecyclerView(binding.rvSubTasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
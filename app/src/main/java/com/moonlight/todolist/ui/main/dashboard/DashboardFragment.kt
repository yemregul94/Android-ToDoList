package com.moonlight.todolist.ui.main.dashboard

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.style
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.databinding.FragmentDashboardBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import com.moonlight.todolist.ui.main.MainViewModel
import com.moonlight.todolist.util.SORT_LIST
import com.moonlight.todolist.util.SwipeDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adapter = DashboardAdapter()
    private var checkedChipIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarDashboard)

        binding.apply {
            fabAddList.setOnClickListener {
                newListItem()
            }
            searchList.setOnQueryTextListener(this@DashboardFragment)
            fabAlarmm.setOnClickListener {
                goToAlarms()
            }
        }

        loadListItems()
        loadSortChoice()
        observeCategoryChips()
        setAdapter()
        setOnRecyclerViewItemSwipedListener()
        addMenu()

        return binding.root
    }

    private fun goToAlarms(){
        val nav = DashboardFragmentDirections.actionDashboardToAlarmList()
        Navigation.findNavController(requireView()).navigate(nav)
    }

    private fun observeCategoryChips() {
        checkedChipIndex = viewModel.selectedCategoryIndex
        binding.chipsCategoryFilter.setOnCheckedStateChangeListener { group, _ ->
            group.forEach {it as Chip
                it.setChipStrokeColorResource(R.color.theme_primary_variant)
                if(!it.isChecked){
                    it.setChipBackgroundColorResource(R.color.not_selected_chip_background)
                    it.setTextColor(resources.getColor(R.color.not_selected_chip_text))
                }else{
                    it.setChipBackgroundColorResource(R.color.selected_chip_background)
                    it.setTextColor(resources.getColor(R.color.selected_chip_text))
                    checkedChipIndex = group.indexOfChild(group.findViewById(it.id))
                    viewModel.selectedCategory = it.text.toString()
                    viewModel.selectedCategoryIndex = checkedChipIndex
                    viewModel.applyFilters()
                }
            }
        }
    }

    private fun loadListItems() {
        viewModel.setUID(authViewModel.uid.toString())
        mainViewModel.getUser(authViewModel.uid)

        mainViewModel.user.observe(viewLifecycleOwner){
            if(it != null){
                loadCategories()
            }
        }

        viewModel.toDoListsItem.observe(viewLifecycleOwner){
            viewModel.applyFilters()
        }

        viewModel.filteredList.observe(viewLifecycleOwner){
            adapter.differ.submitList(it)
        }
    }

    private fun loadSortChoice() {
        viewModel.sortCriteria = viewModel.getSortIndex().toString()
        viewModel.applyFilters()
    }

    private fun loadCategories() {

        createCategoryChip(getString(R.string.all))

        mainViewModel.user.value?.categoryList?.forEach {
            createCategoryChip(it)
        }

        createCategoryChip(getString(R.string.other))

        binding.chipsCategoryFilter.check(binding.chipsCategoryFilter[checkedChipIndex].id)
    }

    private fun createCategoryChip(text: String){
        val chip = Chip(requireContext())
        val chipDrawable = ChipDrawable.createFromAttributes(requireContext(), null, 0, style.Widget_MaterialComponents_Chip_Filter)
        chip.setChipDrawable(chipDrawable)
        chip.ensureAccessibleTouchTarget(0)
        chip.setCheckedIconTintResource(R.color.main_text)

        chip.text = text
        chip.chipStrokeWidth = 3f

        binding.chipsCategoryFilter.addView(chip)
    }

    private fun setAdapter() {
        binding.rvLists.apply {
            adapter = this@DashboardFragment.adapter
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

    private fun newListItem() {
        val nav = DashboardFragmentDirections.actionGoToListDetails(null)
        Navigation.findNavController(requireView()).navigate(nav)
    }


    private fun setOnRecyclerViewItemSwipedListener() {
        ItemTouchHelper(object : SimpleCallback(0, LEFT or RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedListItem = adapter.differ.currentList[position]
                val deletedListItem: ToDoListItem
                if(direction == LEFT){
                    toggleComplete(swipedListItem)
                }
                else if(direction == RIGHT){
                    deleteListItem(swipedListItem.id!!)
                    deletedListItem = swipedListItem

                    Snackbar.make(viewHolder.itemView, getString(R.string.removed, deletedListItem.title), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo)) {
                            viewModel.undoDelete(deletedListItem)
                            binding.rvLists.postDelayed({ binding.rvLists.scrollToPosition(position) }, 100)
                        }
                        .show()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView

                SwipeDecorator().decorateSwiper(itemView, c, dX, recyclerView)

            }

        }).attachToRecyclerView(binding.rvLists)
    }

    private fun duplicateListItem(listItem: ToDoListItem) {
        viewModel.duplicateListItem(listItem, getString(R.string.copy))
    }

    private fun deleteListItem(listID: String){
        viewModel.deleteListItems(listID)
    }

    private fun toggleComplete(listItem: ToDoListItem){
        viewModel.toggleComplete(listItem)
    }

    private fun toggleSubTaskComplete(listItem: ToDoListItem, listPosition: Int, taskPosition: Int){
        viewModel.toggleSubTaskComplete(listItem, taskPosition)
        adapter.notifyItemChanged(listPosition)
    }

    private fun toggleFavorite(listItem: ToDoListItem){
        viewModel.toggleFavorite(listItem)
    }

    private fun toggleArchive(listItem: ToDoListItem){
        viewModel.toggleArchive(listItem)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchQuery(query)
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchQuery(newText)
        return true
    }

    private fun searchQuery(query: String?){
        viewModel.searchQuery = query.toString()
        viewModel.applyFilters()
    }

    private fun addMenu(){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
                menu[1].subMenu?.getItem(SORT_LIST.indexOf(viewModel.sortCriteria))?.isChecked = true
                menu[0].subMenu?.getItem(viewModel.priority+2)?.isChecked = true
                menu.findItem(R.id.menu_filter_fav).isChecked = viewModel.filterFavorites
                menu.findItem(R.id.menu_filter_uncomplete).isChecked = viewModel.hideCompleted
                menu.findItem(R.id.menu_filter_archive).isChecked = viewModel.showArchived
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                menuItem.isChecked = !menuItem.isChecked
                when(menuItem.itemId){
                    R.id.menu_profile -> {
                        goToSettings()
                    }
                    R.id.menu_logout -> {
                        authViewModel.signOut()
                    }
                    R.id.menu_filter_fav -> {
                        viewModel.filterFavorites = menuItem.isChecked
                        viewModel.applyFilters()
                    }
                    R.id.menu_filter_uncomplete -> {
                        viewModel.hideCompleted = menuItem.isChecked
                        viewModel.applyFilters()
                    }
                    R.id.menu_filter_archive -> {
                        viewModel.showArchived = menuItem.isChecked
                        viewModel.applyFilters()
                    }
                    R.id.menu_low -> {
                        setPriorityFilter(0)
                    }
                    R.id.menu_medium -> {
                        setPriorityFilter(1)
                    }
                    R.id.menu_high -> {
                        setPriorityFilter(2)
                    }
                    R.id.menu_top -> {
                        setPriorityFilter(3)
                    }
                    R.id.menu_all -> {
                        setPriorityFilter(-1)
                    }
                    R.id.menu_first_created -> {
                        saveSortChoice("first created")
                    }
                    R.id.menu_last_created -> {
                        saveSortChoice("last created")
                    }
                    R.id.menu_first_updated -> {
                        saveSortChoice("first updated")
                    }
                    R.id.menu_last_updated -> {
                        saveSortChoice("last updated")
                    }
                    R.id.menu_title -> {
                        saveSortChoice("title")
                    }
                    R.id.menu_title_desc -> {
                        saveSortChoice("title reverse")
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun saveSortChoice(choice: String){
        viewModel.sortCriteria = choice
        viewModel.applyFilters()
        viewModel.saveSortIndex(choice)
    }

    private fun setPriorityFilter(priority: Int){
        viewModel.priority = priority
        viewModel.applyFilters()
    }

    fun goToSettings(){
        val nav = DashboardFragmentDirections.actionGoToUserSettings()
        Navigation.findNavController(requireView()).navigate(nav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
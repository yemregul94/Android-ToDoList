package com.moonlight.todolist.ui.main.usersettings

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.UserData
import com.moonlight.todolist.databinding.FragmentUserSettingsBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import com.moonlight.todolist.ui.main.MainViewModel
import com.moonlight.todolist.ui.onboard.OnboardActivity
import com.moonlight.todolist.util.CustomAlertDialog
import com.moonlight.todolist.util.SwipeDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections

@AndroidEntryPoint
class UserSettingsFragment : Fragment() {

    private var _binding: FragmentUserSettingsBinding? = null
    private val binding get() = _binding!!

    private val adapter = UserSettingsAdapter()
    private var categoryList : ArrayList<String> = arrayListOf()
    private var uid: String? = null

    private val viewModel: UserSettingsViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserSettingsBinding.inflate(inflater, container, false)

        uid = authViewModel.uid

        binding.apply {
            expandStatus = false

            layoutSaveGuest.setOnClickListener { saveAnonAccount() }

            layoutChangeEmail.setOnClickListener { updateEmail() }

            layoutChangePassword.setOnClickListener { updatePassword() }

            layoutRemoveData.setOnClickListener { deleteUserData() }

            layoutRemoveUser.setOnClickListener { deleteUser() }

            layoutEditCategories.setOnClickListener { expandStatus = !expandStatus!! }

            btnNewCategory.setOnClickListener { newCategory() }

            btnSaveCategoryList.setOnClickListener { saveCategoryList() }

            checkEditCategory.setOnCheckedChangeListener { _, isChecked -> expandStatus = isChecked }

            layoutAbout.setOnClickListener {
                val nav = UserSettingsFragmentDirections.actionGoToAbout()
                Navigation.findNavController(requireView()).navigate(nav)
            }

            layoutTutorial.setOnClickListener {
                val intent = Intent(this@UserSettingsFragment.context, OnboardActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            layoutLogout.setOnClickListener { authViewModel.signOut() }
        }

        observeCategories()
        observeErrorAndSuccess()
        observeAnonAccount()
        setAdapter()
        setOnRecyclerViewItemSwipedListener()
        getTheme()
        saveTheme()

        return binding.root
    }

    private fun observeAnonAccount() {
        authViewModel.currentUser.observe(viewLifecycleOwner){
            if(it != null){
                binding.isAnon = it.isAnonymous
            }
        }
    }

    private fun observeErrorAndSuccess() {
        viewModel.errorMessage.observe(viewLifecycleOwner){
            if(it != null){
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner){
            if(it != null){
                Toast.makeText(requireContext(), getString(it), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveCategoryList(){
        val userData = UserData(uid.toString(), mainViewModel.user.value!!.userName, categoryList)
        mainViewModel.createUserData(userData, uid)
        Toast.makeText(requireContext(), getString(R.string.category_list_updated), Toast.LENGTH_SHORT).show()
    }

    private fun getTheme(){
        when(mainViewModel.getTheme()){
            "Light" -> binding.toggleGroupTheme.check(binding.btnLight.id)
            "Dark" -> binding.toggleGroupTheme.check(binding.btnDark.id)
            else -> binding.toggleGroupTheme.check(binding.btnSystem.id)
        }
    }

    private fun saveTheme(){
        binding.toggleGroupTheme.addOnButtonCheckedListener { group, _, _ ->
            when(group.checkedButtonId){
                binding.btnLight.id -> mainViewModel.saveTheme("Light")
                binding.btnDark.id -> mainViewModel.saveTheme("Dark")
                else -> mainViewModel.saveTheme("System")
            }
        }
    }

    private fun saveAnonAccount(){ //Firebase calls this anonymous account but users are more familiar with guest account term
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    if(viewModel.saveAnonToEmail(settingTextFirst, settingTextSecond)){
                        authViewModel.getUser()
                    }
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "anon", null, null, positiveButtonClickListener)
    }

    private fun updateEmail(){
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.updateEmail(settingTextFirst)
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "email", authViewModel.currentUser.value?.email, null, positiveButtonClickListener)

    }

    private fun updatePassword(){
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.updatePassword(settingTextFirst)
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "password", null, null, positiveButtonClickListener)

    }

    private fun deleteUserData(){
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.deleteUserData(uid.toString())
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "delete", null, null, positiveButtonClickListener)

    }

    private fun deleteUser(){
        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteUser()
                    authViewModel.getUser()
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "delete", null, null, positiveButtonClickListener)
    }

    private fun observeCategories() {
        mainViewModel.user.observe(viewLifecycleOwner){
            if(it != null){
                categoryList.clear()
                categoryList.addAll(mainViewModel.user.value!!.categoryList)
            }
        }
    }

    private fun setAdapter() {
        adapter.differ.submitList(categoryList)

        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        adapter.onDeleteClick = { position ->
            deleteCategory(position)
        }

        adapter.onItemClick = { position ->
            editCategory(position)
        }
    }

    private fun newCategory(){

        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                if(!categoryList.contains(settingTextFirst) && settingTextFirst != getString(R.string.all) && settingTextFirst != getString(R.string.other)){
                    categoryList.add(settingTextFirst)
                    adapter.notifyItemInserted(adapter.itemCount)
                }
                else{
                    Toast.makeText(requireContext(), getString(R.string.already_exists, settingTextFirst), Toast.LENGTH_SHORT).show()
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "category",null, null, positiveButtonClickListener)
    }

    private fun editCategory(position: Int) {

        val positiveButtonClickListener = object : CustomAlertDialog.CustomAlertListener {
            override fun onPositiveButtonClick(settingTextFirst: String, settingTextSecond: String, check: Boolean) {
                if(!categoryList.contains(settingTextFirst) && settingTextFirst != getString(R.string.all) && settingTextFirst != getString(R.string.other)){
                    categoryList[position] = settingTextFirst
                    adapter.notifyItemChanged(position)
                }
                else{
                    Toast.makeText(requireContext(), getString(R.string.already_exists, settingTextFirst), Toast.LENGTH_SHORT).show()
                }
            }
        }
        CustomAlertDialog().showCustomAlert(requireActivity(), "category", categoryList[position], null, positiveButtonClickListener)
    }

    private fun deleteCategory(position: Int){
        categoryList.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun setOnRecyclerViewItemSwipedListener() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {

                val posOrigin = viewHolder.adapterPosition
                val posTarget = target.adapterPosition

                Collections.swap(categoryList, posOrigin, posTarget)
                adapter.notifyItemMoved(posOrigin, posTarget)

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if(direction == ItemTouchHelper.RIGHT){
                    deleteCategory(position)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView

                SwipeDecorator().decorateSwiper(itemView, c, dX, recyclerView)

            }

        }).attachToRecyclerView(binding.rvCategories)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
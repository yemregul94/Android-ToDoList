package com.moonlight.todolist.ui.auth.reset

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.databinding.FragmentResetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetFragment : Fragment() {

    private var _binding: FragmentResetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResetViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetBinding.inflate(inflater, container, false)


        binding.apply {
            btnReset.setOnClickListener {
                hideKeyboard()
                resetPassword(txtEmail.text.toString())
            }

            txtEmail.addTextChangedListener {
                if(it.toString().isEmpty()){
                    layoutEmail.error = getString(R.string.empty_email)
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches()){
                    layoutEmail.error = getString(R.string.input_valid_email)
                }
                else{
                    layoutEmail.error = null
                }
            }
        }

        observeError()
        return binding.root
    }

    private fun observeError(){
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

    private fun resetPassword(email:String){
        if(email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.resetPassword(email)
            }
        }
        else {
            viewModel.sendError(getString(R.string.input_valid_email))
        }
    }

    private fun hideKeyboard(){
        val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (null != requireActivity().currentFocus) imm.hideSoftInputFromWindow(
            requireActivity().currentFocus!!.applicationWindowToken, 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
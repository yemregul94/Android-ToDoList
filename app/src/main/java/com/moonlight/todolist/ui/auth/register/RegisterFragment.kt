package com.moonlight.todolist.ui.auth.register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.databinding.FragmentRegisterBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            btnRegister.setOnClickListener {
                hideKeyboard()
                register(txtEmail.text.toString(), txtPassword.text.toString())
            }
            txtResetRegister.setOnClickListener {
                goToReset()
            }
            txtLogin.setOnClickListener {
                requireActivity().onBackPressed()
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
            txtPassword.addTextChangedListener {
                if(it.toString().length < 6){
                    layoutPassword.error = getString(R.string.input_valid_password)
                }
                else{
                    layoutPassword.error = null
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
    }

    private fun register(email:String, password:String){
        if(email.isNotEmpty() && password.length >= 6) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.register(email, password)
                authViewModel.getUser()
            }
        }
        else {
            viewModel.sendError(getString(R.string.empty_mail_password))
            if (email.isEmpty()){
                binding.layoutEmail.error = getString(R.string.empty_email)
            }
            if (password.length < 6){
                binding.layoutPassword.error = getString(R.string.input_valid_password)
            }
        }
    }

    private fun goToReset(){
        val nav = RegisterFragmentDirections.actionRegisterToReset()
        Navigation.findNavController(requireView()).navigate(nav)
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
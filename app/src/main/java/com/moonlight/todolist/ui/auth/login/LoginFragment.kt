package com.moonlight.todolist.ui.auth.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.moonlight.todolist.R
import com.moonlight.todolist.databinding.FragmentLoginBinding
import com.moonlight.todolist.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            btnLogin.setOnClickListener {
                hideKeyboard()
                login(txtEmail.text.toString(), txtPassword.text.toString())
            }

            txtRegister.setOnClickListener {
                goToRegister()
            }

            txtResetLogin.setOnClickListener {
                goToReset()
            }

            btnLoginAnon.setOnClickListener {
                loginAsAnon()
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

        getLastLogin()
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

    private fun getLastLogin() {
        if(!authViewModel.getLastLogin().isNullOrEmpty()){
            binding.txtEmail.setText(authViewModel.getLastLogin())
        }
    }

    private fun login(email:String, password:String){
        if(email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.login(email, password)
                authViewModel.getUser()
            }
        }
        else {
            viewModel.sendError(getString(R.string.empty_mail_password))
            if (email.isEmpty()){
                binding.layoutEmail.error = getString(R.string.empty_email)
            }
        }
    }

    private fun loginAsAnon(){
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loginAsAnon()
            authViewModel.getUser()
        }
    }

    private fun goToRegister(){
        val nav = LoginFragmentDirections.actionLoginToRegister()
        Navigation.findNavController(requireView()).navigate(nav)
    }

    private fun goToReset(){
        val nav = LoginFragmentDirections.actionLoginToReset()
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
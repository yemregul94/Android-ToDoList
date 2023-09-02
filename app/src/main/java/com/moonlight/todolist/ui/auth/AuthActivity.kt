package com.moonlight.todolist.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.moonlight.todolist.databinding.ActivityAuthBinding
import com.moonlight.todolist.ui.main.MainActivity
import com.moonlight.todolist.ui.onboard.OnboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkFirstLaunch()
        observeLogin()
        getThemeMode()
    }

    private fun checkFirstLaunch() {
        if(authViewModel.checkFirstLaunch()){
            authViewModel.updateFirstLaunch()
            val intent = Intent(this, OnboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun observeLogin(){
        authViewModel.currentUser.observe(this){
            if(it != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getThemeMode(){
        authViewModel.theme.observe(this){
            when(it){
                "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

}
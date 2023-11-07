package com.moonlight.todolist.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.moonlight.todolist.R
import com.moonlight.todolist.data.model.ToDoListItem
import com.moonlight.todolist.data.model.ToDoSubTask
import com.moonlight.todolist.data.model.UserData
import com.moonlight.todolist.databinding.ActivityMainBinding
import com.moonlight.todolist.ui.auth.AuthActivity
import com.moonlight.todolist.ui.auth.AuthViewModel
import com.moonlight.todolist.util.setAlarm
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeLogin()
        getThemeMode()

        createNotificationChannel()
        getNotificationPermission()
    }

    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ToDoListChannel"
            val descriptionText = "ToDoListChannelDesc"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel("todolist", name, importance)

            channel.description = descriptionText

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun observeLogin(){
        authViewModel.currentUser.observe(this){
            if(it != authViewModel.latestUser){
                authViewModel.latestUser = it

                if(it == null){
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    if(authViewModel.checkNewUser()){
                        Toast.makeText(this, getString(R.string.welcome_first_login), Toast.LENGTH_LONG).show()
                        val user = UserData("uid", "name", arrayListOf(getString(R.string.example_work), getString(R.string.example_home)))
                        mainViewModel.createUserData(user, it.uid)

                        val listItem = ToDoListItem("", getString(R.string.welcome_title), getString(R.string.welcome_desc), 3, false, getString(R.string.example_home),
                            listOf(ToDoSubTask("", getString(R.string.welcome_sub_title1), false), ToDoSubTask("", getString(R.string.welcome_sub_title2), true)),
                            "color4", favorite = false, archived = false,
                            createTime = Calendar.getInstance(Locale.ENGLISH).time.toString(),
                            updateTime = Calendar.getInstance(Locale.ENGLISH).time.toString()
                        )
                        mainViewModel.createFirstListItem(listItem, it.uid)
                    }
                    else {
                        mainViewModel.user.observe(this){ userData ->
                            if (userData != null) {
                                //Toast.makeText(this, getString(R.string.welcome_re_login, userData.userName), Toast.LENGTH_LONG).show()
                                checkAlarms()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAlarms() {
        mainViewModel.setUID(authViewModel.uid!!)

        mainViewModel.toDoListsItem.observe(this){
            val list = it.filter { it.alarmTime.isNotEmpty() }
            list.forEach {
                if(it.alarmTime.isNotEmpty()){
                    setAlarm(this, it)
                }
            }
            mainViewModel.toDoListsItem.removeObservers(this)
        }
    }

    private fun getThemeMode(){
        mainViewModel.theme.observe(this){
            when(it){
                "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}
package com.pachi.smartgreen.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.pachi.smartgreen.NorificationService.NotificationService
import com.pachi.smartgreen.R
import com.pachi.smartgreen.objets.PermissionsManager

class MainActivity : AppCompatActivity() {

    private lateinit var permisos:PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permisos = PermissionsManager(this)
        if (!permisos.arePermissionsGranted()){
            val intent = Intent(applicationContext, SplashActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        setNavController()
    }

    private fun setNavController(){
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.monitoring_action, R.id.conetion_action,R.id.setting_action))
        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        val nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        setupWithNavController(nav, navController)
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (isServiceRunning(NotificationService::class.java)) {
            val serviceIntent = Intent(
                this,
                NotificationService::class.java
            )
            Log.e("Servicio", "Detenido")
            stopService(serviceIntent)
        }
    }


    override fun onPause() {
        super.onPause()
        if (!isServiceRunning(NotificationService::class.java)) {
            val serviceIntent = Intent(
                this,
                NotificationService::class.java
            )
            Log.e("Servicio", "Activado")
            startService(serviceIntent)
        }
    }

}
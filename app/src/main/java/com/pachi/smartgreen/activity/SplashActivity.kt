package com.pachi.smartgreen.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.pachi.smartgreen.R
import com.pachi.smartgreen.objets.PermissionsManager

class SplashActivity : AppCompatActivity() {
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var message: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        permissionsManager = PermissionsManager(this)
        message = findViewById(R.id.messageSplash)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        permissionsManager.checkAndRequestPermissions()
        permissionsManager.alert.observe(this, Observer { it ->
            if (it){
                var seconds = 8
                val thread = Thread {
                    while (true){
                        seconds--
                        runOnUiThread {
                            message?.text = getString(R.string.denied_permiss) + " " + seconds
                        }
                        Thread.sleep(1000)
                        if (seconds <= 1){
                            finish()
                            break
                        }
                    }
                }
                thread.start()
            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.handlePermissionsResult(requestCode, permissions, grantResults)
    }
}
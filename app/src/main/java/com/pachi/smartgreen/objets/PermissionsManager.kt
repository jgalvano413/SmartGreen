package com.pachi.smartgreen.objets

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pachi.smartgreen.R
import com.pachi.smartgreen.activity.MainActivity

class PermissionsManager(private val activity: Activity) {


    private val _alert = MutableLiveData<Boolean>()
    val alert: LiveData<Boolean> get() = _alert
    private val PERMISSION_REQUEST_CODE = 123
    private val REQUEST_BLUETOOTH_PERMISSIONS = 124
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.VIBRATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )


    // Verifica si todos los permisos están concedidos
    fun arePermissionsGranted(): Boolean {
        // Verifica permisos generales
        val generalPermissionsGranted = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
        Log.d("Permisos",generalPermissionsGranted.toString())

        // Verifica permisos de Bluetooth si el SDK es S (API 31) o superior
        val bluetoothPermissionsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            true // En versiones anteriores, estos permisos no son necesarios
        }
        Log.d("Permisos",bluetoothPermissionsGranted.toString())

        return generalPermissionsGranted && bluetoothPermissionsGranted
    }

    // Solicita permisos si es necesario
    fun checkAndRequestPermissions() {
        val permissionsNeeded = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        val bluetoothPermissionsNeeded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
            ).filter {
                ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
            }
        } else {
            emptyList()
        }

        if (permissionsNeeded.isNotEmpty() || bluetoothPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                (permissionsNeeded + bluetoothPermissionsNeeded).toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    fun handlePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    println("${permissions[i]} granted")
                } else {
                    deniedPermissions.add(permissions[i])
                    println("${permissions[i]} denied")
                }
            }

            if (deniedPermissions.isNotEmpty()) {
                _alert.postValue(true)
                val alerta = AlertDialog.Builder(activity)
                alerta.setTitle("ADVERTENCIA")
                    .setMessage(activity.getString(R.string.permiss))
                    .setPositiveButton("OK") { dialog, _ -> activity.finish() }
                alerta.show()
            } else {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
}

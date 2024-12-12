package com.pachi.smartgreen.NorificationService

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pachi.smartgreen.R
import com.pachi.smartgreen.activity.MainActivity
import com.pachi.smartgreen.objets.FirebaseNodes
import com.pachi.smartgreen.objets.NotificationSettings

class NotificationService : Service() {

    //private val handler = Handler()
    /*private val runnable = object : Runnable {
        override fun run() {
            Log.d("MyService", "Servicio en ejecución...")
            handler.postDelayed(this, 5000) // Repetir cada 5 segundos
        }
    }*/
    val PREF_NAME: String = "MyPrefs"
    val KEY_NOTIFICATION_HARDWARE: String = "hardware"
    val CHANNEL_ID: String = "firebase_channel"
    val GROUP_KEY_MESSAGES: String = "group_key_messages"
    val KEY_NOTIFICATION: String = "notification"
    private lateinit var notification:NotificationSettings
    private var childEventListener: ChildEventListener? = null
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var countDownTimer: CountDownTimer? = null
    val data: FirebaseNodes = FirebaseNodes()
    var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(data.First_Node)

    fun init(){
        createNotificationChannel()
        reference.addChildEventListener(childEventListener!!)
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        editor = preferences!!.edit()
        startForeground(1, createNotification("",""))
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("MyService", "Servicio creado")
        //handler.post(runnable) // Inicia la tarea en segundo plano
        timetask()
        listenerData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification = NotificationSettings(this)
        if (!notification!!.getNotificationPermiss()) return START_NOT_STICKY
        init()
        return START_STICKY // Hace que el servicio continúe ejecutándose hasta que sea detenido explícitamente
    }

    override fun onDestroy() {
        super.onDestroy()
        //handler.removeCallbacks(runnable) // Detener el ciclo cuando el servicio sea destruido
        //Log.d("MyService", "Servicio destruido")
        reference.removeEventListener(childEventListener!!)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No es un servicio enlazado
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,
                "Servicio de Notificador",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, CHANNEL_ID
        )
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setSmallIcon(R.drawable.baseline_thermostat_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(GROUP_KEY_MESSAGES)
            .setGroupSummary(true)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)

        return notificationBuilder.build()
    }

    private fun getPendingIntent(): PendingIntent {
        val notificationIntent = Intent(
            this,
            MainActivity::class.java
        )
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun timetask() {
        countDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(
                    "Servicio",
                    "Faltante para reenviar notificacion  " + millisUntilFinished / 1000
                )
                editor!!.putBoolean(KEY_NOTIFICATION, false)
                editor!!.apply()
            }

            override fun onFinish() {
                editor!!.putBoolean(KEY_NOTIFICATION, true)
                editor!!.apply()
            }
        }.start()
    }

    private fun listenerData(){
        childEventListener = object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val key = snapshot.key
                val data = snapshot.value
                notify(data!!,key!!)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

    }

    private fun notify(data:Any,key:String){
        if (preferences!!.getBoolean(KEY_NOTIFICATION, false)) {
            try {
                when (data) {
                    is String -> {
                        if (notification.getData(key!!) != data) {
                            val notificationTemp = notification.getData(key!!)!!.toInt()
                            val temp = data.toInt()
                            if (temp >= 28) {
                                startForeground(1,createNotification("ADVERTENCIA", "Temperatura Alta en " + key + " de " + data + "°C"))
                            }
                            if (notificationTemp > temp) {
                                startForeground(1,createNotification("BAJO TEMPERATURA", "La temperatura de " + key + "  bajo a " + data + "°C"))
                            } else if (notificationTemp < temp) {
                                startForeground(1,createNotification("ALTA TEMPERATURA", "La temperatura de " + key + "  subio a " + data + "°C"))
                            }
                            timetask()
                        }
                    }

                    is Boolean -> {
                        val userPreference = notification.getMovving(key!!)
                        when (userPreference) {
                            true -> {
                                if (data is Boolean && data) {
                                    Log.e("Notificacion", "$key Se ha Activado")
                                    startForeground(1, createNotification("ENCENDIDO", "$key Se ha Activado"))
                                }
                            }
                            false -> {
                                if (data is Boolean && !data) {
                                    Log.e("Notificacion", "$key Se ha Apagado")
                                    startForeground(1, createNotification("APAGADO", "$key Se ha Apagado"))
                                }
                            }
                        }

                        timetask()
                    }
                }
            } catch (e:Exception){
                Log.e("error",e.message.toString())
                startForeground(1, createNotification("ERROR","No se puedo generar la Notificacion por un error en la Base de Datos"))
            }
        }
    }
}
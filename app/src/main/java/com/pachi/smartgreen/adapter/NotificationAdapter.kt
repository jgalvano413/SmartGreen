package com.pachi.smartgreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.pachi.smartgreen.R
import com.pachi.smartgreen.objets.NotificationSettings
import com.pachi.smartgreen.objets.dataBaseObject

class NotificationAdapter(private val notificationList: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Identificadores de vista
    private val TYPE_HARDWARE = 0
    private val TYPE_SOFTWARE = 1
    private lateinit var NotificationSettings:NotificationSettings
    private lateinit var adapter:ArrayAdapter<String>
    private lateinit var context: Context
    val list = mutableListOf("Encendido", "Apagado")

    // ViewHolder para hardware
    class HardwareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mototrsKay: TextView = itemView.findViewById(R.id.textViewNameMotors)
        val button: Button = itemView.findViewById(R.id.btnMotors)
        val spinner: Spinner = itemView.findViewById(R.id.warningHard)
    }

    // ViewHolder para software
    class SoftwareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textViewNamePlants)
        val button: Button = itemView.findViewById(R.id.btnSave)
        val amount: AppCompatEditText = itemView.findViewById(R.id.amountData)
    }

    override fun getItemViewType(position: Int): Int {
        // Diferenciar entre hardware y software
        return when (notificationList[position]) {
            is dataBaseObject.hardware -> TYPE_HARDWARE
            is dataBaseObject.software -> TYPE_SOFTWARE
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        NotificationSettings = NotificationSettings(parent.context)
        adapter = ArrayAdapter(parent.context, R.layout.item_spinner, R.id.textViewSpinner, list)
        adapter.setDropDownViewResource(R.layout.item_spinner)
        context = parent.context
        return when (viewType) {
            TYPE_HARDWARE -> {
                // Inflar diseño para hardware
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hardware_settings, parent, false)
                HardwareViewHolder(view)
            }
            TYPE_SOFTWARE -> {
                // Inflar diseño para software
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_software_settings, parent, false)
                SoftwareViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = notificationList[position]

        when (holder) {
            is HardwareViewHolder -> {
                // Configurar el ViewHolder para hardware
                val hardwareItem = item as dataBaseObject.hardware
                var data = ""
                holder.spinner.adapter = adapter
                holder.spinner.setSelection(adapter.getPosition(stringConverter(NotificationSettings.getMovving(hardwareItem.key))))
                holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?,view: View?, position: Int, id: Long) {
                        data = list[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

                holder.mototrsKay.text = hardwareItem.key
                holder.button.setOnClickListener {
                    NotificationSettings.saveThresholdMachine(hardwareItem.key,booleanConevrter(data))
                    Toast.makeText(context, "Configuracion de Notificacion Guardada", Toast.LENGTH_SHORT).show()
                }
            }
            is SoftwareViewHolder -> {
                // Configurar el ViewHolder para software
                val softwareItem = item as dataBaseObject.software
                holder.name.text = "Notifiacion para " + softwareItem.key
                holder.amount.setText(NotificationSettings.getData(softwareItem.key))
                holder.button.setOnClickListener {
                    val data = holder.amount.text.toString()
                    if (data!!.equals(NotificationSettings.getData(softwareItem.key))){
                        holder.amount.setError("Cambio no Aplicado")
                    } else {
                        NotificationSettings.saveThreshold(softwareItem.key,data)
                        Toast.makeText(context, "Configuracion de Notificacion Guardada", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        holder.itemView.setTranslationY(3000F);
        holder.itemView.animate().translationY(0F).setDuration(500).start();
    }

    fun stringConverter(data:Boolean):String{
        if (data){
            return "Encendido"
        } else {
            return "Apagado"
        }
    }


    fun booleanConevrter(data:String):Boolean{
        if (data.equals("Encendido")){
            return true
        } else {
            return false
        }
    }


    override fun getItemCount(): Int {
        return notificationList.size
    }
}

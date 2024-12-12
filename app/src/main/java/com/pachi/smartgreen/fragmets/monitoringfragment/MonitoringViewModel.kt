package com.pachi.smartgreen.fragmets.monitoringfragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pachi.smartgreen.objets.FirebaseNodes
import com.pachi.smartgreen.objets.dataBaseObject
import java.util.Objects

class MonitoringViewModel : ViewModel() {

    val data:FirebaseNodes = FirebaseNodes()
    var reference: DatabaseReference = FirebaseDatabase.getInstance().getReference(data.First_Node)

    fun conectBBD(data:MutableLiveData<Boolean>){
        reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    data.postValue(true)
                } else {
                    data.postValue(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                data.postValue(false)
            }
        })
    }

    fun getData(): List<Any> {
        val dataList = mutableListOf<Any>()
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        try {
                            val key = dataSnapshot.key
                            val dataValue = dataSnapshot.getValue()
                            val reference = dataSnapshot.ref
                            if (key!!.contains(data.Plant_Node_KEY)) {
                                dataList.add(
                                    dataBaseObject.software(
                                        key.toString(),
                                        dataValue.toString(),
                                        reference
                                    )
                                )
                            } else {
                                val booleanValue =
                                    dataValue as? Boolean ?: false // Asegúrate de que sea Boolean
                                dataList.add(dataBaseObject.hardware(key, booleanValue, reference))
                            }
                        } catch (e:Exception) {
                            Log.e("Object Error",e.message.toString())
                        }
                    }
                } else {
                    dataList.add(dataBaseObject.software("Sin Datos", "N/A", reference))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Gasatumadre", error.message)
            }
        })
        return dataList
    }



}


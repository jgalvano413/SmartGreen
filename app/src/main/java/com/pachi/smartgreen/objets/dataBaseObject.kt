package com.pachi.smartgreen.objets

import com.google.firebase.database.DatabaseReference

class dataBaseObject{

    class hardware(val key:String,val action:Boolean,val reference: DatabaseReference)

    class software(val key:String,val data:String,val reference: DatabaseReference)
}
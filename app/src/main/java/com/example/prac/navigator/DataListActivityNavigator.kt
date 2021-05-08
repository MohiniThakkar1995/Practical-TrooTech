package com.example.jetpackdemo

import com.example.prac.model.ModelDataInfoToStoreInRoomDB


interface DataListActivityNavigator {
    fun removeLastPositionFromList()
    fun sendListToRoomDB( list : ArrayList<ModelDataInfoToStoreInRoomDB?>)


}
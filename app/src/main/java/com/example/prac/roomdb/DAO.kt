package com.example.jetpackdemo.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface DAO {
    @Query("SELECT * FROM data_table")
    fun allData(): List<Data>

    @Query("SELECT * FROM data_table WHERE idmenu =:idmenu")
    fun getLiveOrderById(idmenu: String): Data?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(modelStoreDataToRoomDB: Data?)

    @Query("UPDATE data_table SET precioSugerido = :precioSugerido, qty = :qty WHERE idmenu =:idmenu")
    fun update(precioSugerido: String?, qty: String?, idmenu: String)
}
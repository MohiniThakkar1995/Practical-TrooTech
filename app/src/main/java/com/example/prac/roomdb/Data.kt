package com.example.jetpackdemo.roomdb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/*
@Entity
class Data {
    @PrimaryKey(autoGenerate = true)
    private val id = 0
    private val _id = 0
    private val name: String? = null


}
*/

@Entity(tableName = "data_table")
data class Data(
    @PrimaryKey
    @ColumnInfo(name = "idmenu")
    var idmenu: String,
    @ColumnInfo(name = "precioSugerido")
    var precioSugerido: String,
    @ColumnInfo(name = "qty")
    var qty: String,
    @ColumnInfo(name = "nombre")
    var nombre: String,
    @ColumnInfo(name = "nombremenu")
    var nombremenu: String
)
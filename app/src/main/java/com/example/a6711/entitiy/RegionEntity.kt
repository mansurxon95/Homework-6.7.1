package com.example.a6711.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RegionEntity (

    @PrimaryKey(autoGenerate = true)
    var regionId:Int?=null,
    @ColumnInfo(name = "regionName")
    var regionName:String?=null,

    )
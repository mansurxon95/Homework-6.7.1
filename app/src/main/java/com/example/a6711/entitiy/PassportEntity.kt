package com.example.a6711.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
    entity = RegionEntity::class,
    parentColumns = arrayOf("regionId"),
    childColumns = arrayOf("region")
    )]
)
data class PassportEntity (

    @PrimaryKey(autoGenerate = true)
    var passportId:Long?=null,
    @ColumnInfo(name = "image")
    var image:String?=null,
    @ColumnInfo(name = "lastName")
    var lastName:String?=null,
    @ColumnInfo(name = "firstName")
    var firstName:String?=null,
    @ColumnInfo(name = "fatherName")
    var fatherName:String?=null,
    @ColumnInfo(name = "region")
    var region:Int?=null,
    @ColumnInfo(name = "city")
    var city:String?=null,
    @ColumnInfo(name = "homeAddress")
    var homeAddress:String?=null,
    @ColumnInfo(name = "passportTakeTime")
    var passportTakeTime:String?=null,
    @ColumnInfo(name = "passportGivenTime")
    var passportGivenTime:String?=null,
    @ColumnInfo(name = "pass")
    var sex:String?=null
):Serializable
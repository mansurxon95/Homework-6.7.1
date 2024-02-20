package com.example.a6711.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.a6711.entitiy.RegionEntity


@Dao
interface RegionDao {

    @Insert
    fun addRegion(regionEntity: RegionEntity)

    @Query("select * from RegionEntity")
    fun getAllRegionString():List<RegionEntity>

    @Query("select regionId from RegionEntity")
    fun getAllRegionId():List<Int>



}
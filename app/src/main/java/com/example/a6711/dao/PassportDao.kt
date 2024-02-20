package com.example.a6711.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.a6711.entitiy.PassportEntity

@Dao
interface PassportDao {

    @Query("select*from PassportEntity")
    fun getAllPassport():List<PassportEntity>

    @Insert
    fun addPassport(passportUser: PassportEntity)

//    @Query("UPDATE PassportEntity SET passportId =:id")
//    fun updatePassport(passportUser: PassportEntity, id:Long)
    @Update
    fun updatePassport(passportUser: PassportEntity)

    @Delete
    fun deletePassport(passportUser: PassportEntity)

    @Query("DELETE FROM PassportEntity WHERE passportId = :userId")
    fun deletePassportId(userId: Long)

}
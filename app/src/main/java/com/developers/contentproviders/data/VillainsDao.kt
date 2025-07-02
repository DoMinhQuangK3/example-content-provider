package com.developers.contentproviders.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy
import android.database.Cursor
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Villains table
 */
@Dao
interface VillainsDao {

    @Query("SELECT COUNT(*) FROM ${Villains.TABLE_NAME}")
    suspend fun count(): Int

    @Query("SELECT * FROM ${Villains.TABLE_NAME}")
    fun selectAll(): Cursor

    @Query("SELECT * FROM ${Villains.TABLE_NAME}")
    fun getAllVillainsFlow(): Flow<List<Villains>>

    @Query("SELECT * FROM ${Villains.TABLE_NAME}")
    suspend fun getAllVillains(): List<Villains>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(villain: Villains): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(villains: List<Villains>)

    @Query("DELETE FROM ${Villains.TABLE_NAME} WHERE ${Villains.COLUMN_ID} = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM ${Villains.TABLE_NAME} WHERE ${Villains.COLUMN_ID} = :id")
    fun selectById(id: Long): Cursor

    @Query("SELECT * FROM ${Villains.TABLE_NAME} WHERE ${Villains.COLUMN_ID} = :id")
    suspend fun getVillainById(id: Long): Villains?

    @Update
    suspend fun update(villain: Villains): Int

    @Query("DELETE FROM ${Villains.TABLE_NAME}")
    suspend fun deleteAll()
}
package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecimenDao {
    @Query("SELECT * FROM specimens ORDER BY id ASC")
    fun getAllSpecimens(): Flow<List<SpecimenEntity>>

    @Query("SELECT * FROM specimens WHERE specimenCode = :code LIMIT 1")
    fun getSpecimenByCode(code: String): Flow<SpecimenEntity?>

    @Query("SELECT * FROM specimens WHERE specimenCode = :code LIMIT 1")
    suspend fun findSpecimenByCode(code: String): SpecimenEntity?

    @Query("UPDATE specimens SET aggression = :aggression, bioLuminescence = :bioLuminescence, dermalHardening = :dermalHardening, status = 'MUTATING' WHERE specimenCode = :code")
    suspend fun updateModifiers(code: String, aggression: Int, bioLuminescence: Int, dermalHardening: Int)

    @Query("SELECT * FROM specimens WHERE id = :id LIMIT 1")
    suspend fun getSpecimenById(id: Int): SpecimenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecimen(specimen: SpecimenEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecimens(specimens: List<SpecimenEntity>)

    @Update
    suspend fun updateSpecimen(specimen: SpecimenEntity)

    @Query("DELETE FROM specimens WHERE id = :id")
    suspend fun deleteSpecimenById(id: Int)

    @Query("SELECT * FROM mutation_logs ORDER BY timestamp DESC LIMIT 40")
    fun getAllMutationLogs(): Flow<List<MutationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMutationLog(log: MutationLogEntity)

    @Query("DELETE FROM mutation_logs")
    suspend fun clearMutationLogs()

    @Query("SELECT COUNT(*) FROM specimens")
    suspend fun getSpecimenCount(): Int
}

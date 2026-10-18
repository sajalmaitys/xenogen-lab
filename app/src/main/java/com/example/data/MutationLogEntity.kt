package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mutation_logs")
data class MutationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val specimenCode: String,
    val message: String,
    val timeLabel: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String = "MUTATION" // INFO, MUTATION, ALERT, SPLICED
)

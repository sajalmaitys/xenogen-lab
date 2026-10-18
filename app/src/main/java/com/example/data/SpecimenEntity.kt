package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "specimens")
data class SpecimenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val specimenCode: String,
    val commonName: String,
    val genus: String,
    val originPlanet: String,
    val status: String, // STABILIZED, MUTATING, STABLE, EVOLVING
    val dnaSequence: String,
    val aggression: Int,
    val intelligence: Int,
    val venomGlands: Boolean = true,
    val dermalHardening: Int = 50,
    val bioLuminescence: Int = 50,
    val imageUrl: String,
    val habitatType: String, // Cryo, Liquid, Bio, Aero, Pyro, Void
    val notes: String = "",
    val createdTimestamp: Long = System.currentTimeMillis()
)

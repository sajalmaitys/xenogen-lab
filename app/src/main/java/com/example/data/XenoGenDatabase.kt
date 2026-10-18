package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SpecimenEntity::class, MutationLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class XenoGenDatabase : RoomDatabase() {
    abstract fun specimenDao(): SpecimenDao

    companion object {
        @Volatile
        private var INSTANCE: XenoGenDatabase? = null

        fun getInstance(context: Context): XenoGenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XenoGenDatabase::class.java,
                    "xenogen_database.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

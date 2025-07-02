package com.developers.contentproviders.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for storing Villains data
 */
@Database(
    entities = [Villains::class], 
    version = 1,
    exportSchema = false
)
abstract class VillainsDatabase : RoomDatabase() {

    abstract fun villainDao(): VillainsDao

    companion object {
        @Volatile
        private var INSTANCE: VillainsDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): VillainsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VillainsDatabase::class.java,
                    "villains_database"
                )
                .addCallback(VillainsDatabaseCallback(scope))
                .build()
                
                INSTANCE = instance
                instance
            }
        }

        private class VillainsDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.villainDao())
                    }
                }
            }
        }

        private suspend fun populateDatabase(villainDao: VillainsDao) {
            // Delete all content here
            villainDao.deleteAll()

            // Add sample villains
            val sampleVillains = Villains.SAMPLE_VILLAINS.map { (name, series) ->
                Villains(villainName = name, villainSeries = series)
            }
            
            villainDao.insertAll(sampleVillains)
        }
    }
}
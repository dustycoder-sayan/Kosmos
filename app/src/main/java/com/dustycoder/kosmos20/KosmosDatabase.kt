package com.dustycoder.kosmos20

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BoardEntity::class, ListEntity::class, CardEntity::class, NotesEntity::class,
                     ChecklistEntity::class],
    version = 1)
abstract class KosmosDatabase : RoomDatabase() {

    abstract fun kosmosDao():KosmosDao

    companion object {

        @Volatile
        private var INSTANCE: KosmosDatabase? = null

        // singleton instance return
        fun getInstance(context: Context): KosmosDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        KosmosDatabase::class.java,
                        "kosmos-database"
                    ).allowMainThreadQueries().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
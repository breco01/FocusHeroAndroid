package com.bcornet.focushero.data

import android.content.Context
import androidx.room.Room
import com.bcornet.focushero.data.db.AppDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase{
        return INSTANCE ?: synchronized(this){
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "focushero.db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
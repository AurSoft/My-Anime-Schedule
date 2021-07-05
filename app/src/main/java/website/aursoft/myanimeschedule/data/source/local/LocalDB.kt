package website.aursoft.myanimeschedule.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Singleton class that is used to create a anime db
 */
object LocalDB {
    private lateinit var database: AnimeDatabase
    /**
     * static method that creates a anime class and returns the DAO of the anime
     */
    fun createAnimeDAO(context: Context): AnimeDAO {
        if (!this::database.isInitialized) { // because I'm accessing the db from a worker too, I don't want to have multiple instances of this db created
            database = Room.databaseBuilder(
                context.applicationContext,
                AnimeDatabase::class.java,
                "animeSchedule.db"
            ).build()
        }
        return database.animeDAO()
    }
}
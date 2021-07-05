package website.aursoft.myanimeschedule.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AnimeDTO::class], version = 1, exportSchema = false)
abstract class AnimeDatabase: RoomDatabase() {
    abstract fun animeDAO(): AnimeDAO
}
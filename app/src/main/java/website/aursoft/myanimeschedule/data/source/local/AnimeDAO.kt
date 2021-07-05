package website.aursoft.myanimeschedule.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import website.aursoft.myanimeschedule.utils.getTodayAsString

@Dao
interface AnimeDAO {

    @Query("SELECT * FROM anime")
    fun observeAllAnime(): LiveData<List<AnimeDTO>>

    @Query("SELECT * FROM anime WHERE is_watching = 1")
    fun observeAllWatchedAnime(): LiveData<List<AnimeDTO>>

    @Query("SELECT * FROM anime WHERE is_watching = 1")
    suspend fun getAllWatchedAnime(): List<AnimeDTO>

    @Query("SELECT * FROM anime where mal_id = :animeId")
    suspend fun getAnimeById(animeId: String): AnimeDTO?

    @Query("SELECT * FROM anime where schedule_day = :day")
    suspend fun getScheduleByDay(day: String = getTodayAsString()): List<AnimeDTO>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveAnime(vararg animeDTO: AnimeDTO)

    @Query("DELETE FROM anime WHERE is_watching = 0 and is_completed = 0")
    suspend fun deleteCache()
    /**
     * Returns -1 if anime already saved
     */
    @Update
    suspend fun updateAnime(vararg animeDTO: AnimeDTO): Int

    /**
     * Returns -1 if anime already saved
     */
    @Update(entity = AnimeDTO::class)
    suspend fun updateAnime(vararg cachedAnimeDTO: CachedAnimeDTO): Int

    @Query("DELETE FROM anime WHERE mal_id = :malId")
    suspend fun deleteAnime(malId: String)
}
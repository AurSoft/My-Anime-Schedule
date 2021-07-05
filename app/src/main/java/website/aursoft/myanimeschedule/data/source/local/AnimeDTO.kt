package website.aursoft.myanimeschedule.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import website.aursoft.myanimeschedule.data.Aired
import website.aursoft.myanimeschedule.data.Anime

@Entity(tableName = "anime")
data class AnimeDTO (
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "image_url") var imageUrl: String?,
    @ColumnInfo(name = "trailer_url") var trailerUrl: String?,
    @ColumnInfo(name = "type") var type: String?,
    @ColumnInfo(name = "source") var source: String?,
    @ColumnInfo(name = "episodes") var episodes: Int?,
    @ColumnInfo(name = "status") var status: String?,
    @ColumnInfo(name = "airing") var airing: Boolean = false,
    @ColumnInfo(name = "aired_from") var airedFrom: String?,
    @ColumnInfo(name = "aired_to") var airedTo: String?,
    @ColumnInfo(name = "duration") var duration: String?,
    @ColumnInfo(name = "rating") var rating: String?,
    @ColumnInfo(name = "score") var score: Double?,
    @ColumnInfo(name = "synopsis") var synopsis: String?,
    @ColumnInfo(name = "background") var background: String?,
    @ColumnInfo(name = "broadcast") var broadcast: String?,
    @ColumnInfo(name = "broadcast_local") var broadcastLocal: String?,
    @ColumnInfo(name = "is_watching") var isWatching: Boolean = false,
    @ColumnInfo(name = "is_completed") var isCompleted: Boolean = false,
    @ColumnInfo(name = "schedule_day") var scheduleDay: String?,
    @PrimaryKey @ColumnInfo(name = "mal_id")val malId: String
)

/**
 * Use this DTO to update everything but is_watching, is_completed and broadcast_local fields
 */
data class CachedAnimeDTO (
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "image_url") var imageUrl: String?,
    @ColumnInfo(name = "trailer_url") var trailerUrl: String?,
    @ColumnInfo(name = "type") var type: String?,
    @ColumnInfo(name = "source") var source: String?,
    @ColumnInfo(name = "episodes") var episodes: Int?,
    @ColumnInfo(name = "status") var status: String?,
    @ColumnInfo(name = "airing") var airing: Boolean = false,
    @ColumnInfo(name = "aired_from") var airedFrom: String?,
    @ColumnInfo(name = "aired_to") var airedTo: String?,
    @ColumnInfo(name = "rating") var rating: String?,
    @ColumnInfo(name = "score") var score: Double?,
    @ColumnInfo(name = "synopsis") var synopsis: String?,
    @ColumnInfo(name = "background") var background: String?,
    @ColumnInfo(name = "broadcast") var broadcast: String?,
    @ColumnInfo(name = "schedule_day") var scheduleDay: String?,
    @PrimaryKey @ColumnInfo(name = "mal_id")val malId: String
)

fun fromAnimeDTOToAnime(animeDTO: AnimeDTO): Anime {
    return Anime(
        title = animeDTO.title ?: "",
        imageUrl = animeDTO.imageUrl ?: "",
        trailerUrl = animeDTO.trailerUrl ?: "",
        type = animeDTO.type ?: "",
        source = animeDTO.source ?: "",
        episodes = animeDTO.episodes ?: 0,
        status = animeDTO.status ?: "",
        airing = animeDTO.airing,
        aired = Aired(animeDTO.airedFrom ?: "", animeDTO.airedTo ?: ""),
        duration = animeDTO.duration ?: "",
        rating = animeDTO.rating ?: "",
        score = animeDTO.score ?: 0.0,
        synopsis = animeDTO.synopsis ?: "",
        background = animeDTO.background ?: "",
        broadcast = animeDTO.broadcast ?: "",
        broadcastLocal = animeDTO.broadcastLocal ?: "",
        isWatching = animeDTO.isWatching,
        isCompleted = animeDTO.isCompleted,
        scheduleDay = animeDTO.scheduleDay ?: "",
        malId = animeDTO.malId
    )
}

fun AnimeDTO.asDomainModel(): Anime {
    return fromAnimeDTOToAnime(this)
}

fun List<AnimeDTO>.asDomainModel(): List<Anime> {
    return map {
        fromAnimeDTOToAnime(it)
    }
}
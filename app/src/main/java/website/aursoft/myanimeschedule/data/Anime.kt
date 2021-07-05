package website.aursoft.myanimeschedule.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import website.aursoft.myanimeschedule.data.source.local.AnimeDTO
import website.aursoft.myanimeschedule.data.source.local.CachedAnimeDTO

/**
 * Immutable model class for a Anime.
 */

@Parcelize
data class Anime (
    var title: String = "",
    @Json(name = "image_url") var imageUrl: String = "",
    @Json(name = "trailer_url") var trailerUrl: String? = "",
    var type: String = "",
    var source: String = "",
    var episodes: Int? = 0,
    var status: String = "",
    var airing: Boolean,
    var aired: Aired,
    var duration: String = "",
    var rating: String = "",
    var score: Double? = 0.0,
    var synopsis: String = "",
    var background: String? = "",
    var broadcast: String = "",
    var broadcastLocal: String = "",
    var isWatching: Boolean = false,
    var isCompleted: Boolean = false,
    var scheduleDay: String = "",
    @Json(name = "mal_id")val malId: String = "") : Parcelable

fun Anime.asAnimeDTO(): AnimeDTO {
    return AnimeDTO(
            title = title,
            imageUrl = imageUrl,
            trailerUrl = trailerUrl,
            type = type,
            source = source,
            episodes = episodes,
            status = status,
            airing = airing,
            airedFrom = aired.from,
            airedTo = aired.to,
            duration = duration,
            rating = rating,
            score = score,
            synopsis = synopsis,
            background = background,
            broadcast = broadcast,
            broadcastLocal = broadcastLocal,
            scheduleDay = scheduleDay,
            isWatching = isWatching,
            isCompleted = isCompleted,
            malId = malId
        )
}

fun Anime.asCachedAnimeDTO(): CachedAnimeDTO {
    return CachedAnimeDTO(
        title = title,
        imageUrl = imageUrl,
        trailerUrl = trailerUrl,
        type = type,
        source = source,
        episodes = episodes,
        status = status,
        airing = airing,
        airedFrom = aired.from,
        airedTo = aired.to,
        rating = rating,
        score = score,
        synopsis = synopsis,
        background = background,
        broadcast = broadcast,
        scheduleDay = scheduleDay,
        malId = malId
    )
}
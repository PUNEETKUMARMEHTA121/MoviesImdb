package com.example.popularmovies.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.popularmovies.data.GenreListConverter
import com.example.popularmovies.data.ProductionCompanyListConverter
import com.example.popularmovies.data.ProductionCountryListConverter
import com.example.popularmovies.data.SpokenLanguageListConverter
import com.google.gson.annotations.SerializedName

@Entity
data class MovieDetailModel(
    @PrimaryKey val id: Int,
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val budget: Int?,
    val genres: List<Genre>?,
    val homepage: String?,
    val imdbId: String?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>?,
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>?,
    @SerializedName("release_date")
    val releaseDate: String?,
    val revenue: Int?,
    val runtime: Int?,
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?
) {
    companion object {
        fun empty() = MovieDetailModel(
            -1,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
//            null,
            null
        )
    }

    fun generateGenreString(): String {
        return genres
            ?.mapNotNull { it.name }
            ?.joinToString(separator = ", ") ?: ""
    }

    fun generateSpokenLanguagesString(): String {
        return spokenLanguages
            ?.mapNotNull { it.englishName }
            ?.joinToString(separator = ", ") ?: ""
    }

    fun generateProductionCountriesString(): String {
        return productionCountries
            ?.mapNotNull { it.name }
            ?.joinToString(separator = ", ") ?: ""
    }
}

data class Genre(
    val id: Int?,
    val name: String?
)

data class ProductionCompany(
    val id: Int?,
    @SerializedName("logo_path")
    val logoPath: String?,
    val name: String?,
    @SerializedName("origin_country")
    val originCountry: String?
)

data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso31661: String?,
    val name: String?
)

data class SpokenLanguage(
    @SerializedName("english_name")
    val englishName: String?,
    @SerializedName("iso_639_1")
    val iso6391: String?,
    val name: String?
)


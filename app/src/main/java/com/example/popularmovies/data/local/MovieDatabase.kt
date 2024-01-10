package com.example.popularmovies.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.popularmovies.data.*
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.data.model.MovieModel

@Database(
    entities = [MovieDetailModel::class, MovieModel::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    IntegerListConverter::class,
    GenreListConverter::class,
    ProductionCompanyListConverter::class,
    ProductionCountryListConverter::class,
    SpokenLanguageListConverter::class
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
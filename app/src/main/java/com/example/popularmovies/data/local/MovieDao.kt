package com.example.popularmovies.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.model.MovieResponse

@Dao
interface MovieDao {
    // MovieModel operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPopularMovies(movies: List<MovieModel>)

    @Query("SELECT * FROM MovieModel " +
            "ORDER BY pageNumber ASC " +
            "LIMIT  :pageSize " +
            " OFFSET  (:page - 1) * :pageSize")
    suspend fun getPagedMovies(page: Int, pageSize: Int): List<MovieModel>

    @Query("DELETE FROM MovieModel")
    suspend fun deletePopularMovies()

    // MovieDetailModel operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetail(movieDetail: MovieDetailModel)

    @Query("SELECT * FROM MovieDetailModel WHERE id = :movieId")
    suspend fun getMovieDetail(movieId: Int): MovieDetailModel?
}
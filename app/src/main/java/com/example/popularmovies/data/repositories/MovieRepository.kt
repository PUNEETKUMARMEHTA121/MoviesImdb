package com.example.popularmovies.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.enums.GenreFilter
import com.example.popularmovies.data.enums.SortOption
import com.example.popularmovies.data.local.MovieDao
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.model.Result
import com.example.popularmovies.data.network.MovieApiService
import com.example.popularmovies.data.pagination.MoviePagingSource
import java.io.IOException

class MovieRepository(private val movieApi: MovieApiService, private val movieDao: MovieDao) {

    var moviePagingSource: MoviePagingSource? = null
    private var genreFilter: GenreFilter = GenreFilter.ALL
    private var sortOption: SortOption = SortOption.DEFAULT

    fun getPopularMovies(context: Context): LiveData<PagingData<MovieModel>> {
        moviePagingSource = MoviePagingSource(movieApi, movieDao, context)
        return Pager(
            config = PagingConfig(pageSize = Utility.PAGE_SIZE),
            pagingSourceFactory = {moviePagingSource!! }
        ).liveData
    }

    suspend fun getMovieDetails(
        movieId: Int,
        apiKey: String,
        context: Context
    ): Result<MovieDetailModel> {
        // Implement API call to get movie details
        return try {
            if (Utility.isOnline(context)) {
                // Fetch data from the network
                val response = movieApi.getMovieDetail(movieId, apiKey)
                if (response.isSuccessful) {
                    // Update the local database with the network data
                    val networkData = response.body()
                    networkData?.let {
                        movieDao.insertMovieDetail(it)
                        Result.Success(it)
                    } ?: Result.Failure(IOException("No data received from the server"))
                } else {
                    Result.Failure(IOException("Error fetching data from the server"))
                }
            } else {
                // Fetch data from the local database
                 return movieDao.getMovieDetail(movieId)?.let { localMovieDetail ->
                      Result.Success(localMovieDetail)
                  } ?: Result.Failure(IOException("No data received from the server"))
            }
        } catch (e: IOException) {
            // Handle IOException separately
            Result.Failure(e)
        } catch (e: Exception) {
            // Handle other exceptions
            Result.Failure(IOException("An unexpected error occurred"))
        }
    }

    fun updateFilterAndSort(genreFilter: GenreFilter, sortOption: SortOption, context: Context) {
        this.genreFilter = genreFilter
        this.sortOption = sortOption
        moviePagingSource = MoviePagingSource(movieApi, movieDao, context)
        moviePagingSource?.invalidate()
    }
}
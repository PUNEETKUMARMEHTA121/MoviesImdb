package com.example.popularmovies.data.pagination

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.enums.GenreFilter
import com.example.popularmovies.data.enums.SortOption
import com.example.popularmovies.data.local.MovieDao
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.network.MovieApiService
import retrofit2.HttpException
import java.io.IOException

class MoviePagingSource(
    private val movieApi: MovieApiService,
    private val movieDao: MovieDao,
    private val context: Context
) :
    PagingSource<Int, MovieModel>() {

    private var genreFilter: GenreFilter = GenreFilter.ALL
    private var sortOption: SortOption = SortOption.DEFAULT

    fun updateFilterAndSort(genreFilter: GenreFilter, sortOption: SortOption) {
        this.genreFilter = genreFilter
        this.sortOption = sortOption
        invalidate()
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieModel> {
        try {
            val currentPage = params.key ?: 1
            var movies:List<MovieModel> = listOf()

            if (Utility.isOnline(context)) {
                // User is online, fetch data from remote
                val response =
                    movieApi.getPopularMovies(apiKey = Utility.API_KEY, page = currentPage)

                if (response.isSuccessful) {
                     movies = response.body()?.results ?: emptyList()

                    val moviesWithPageNumber = movies.map { movie ->
                        movie.copy(pageNumber = currentPage)
                    }

                    // Save data to the database
                    if (currentPage == 1) {
                        movieDao.deletePopularMovies()
                    }
                    movieDao.insertPopularMovies(moviesWithPageNumber)
                } else {
                    return LoadResult.Error(Exception("Error loading data"))
                }
            } else {
                // User is offline, fetch data from local database
                 movies = movieDao.getPagedMovies(
                    currentPage,
                    Utility.PAGE_SIZE
                )
            }
            // Apply filter and sort
            val filteredAndSortedMovies = applyFilterAndSort(movies)

            return LoadResult.Page(
                data = filteredAndSortedMovies,
                prevKey = if (currentPage > 1) currentPage - 1 else null,
                nextKey = if (movies.isNotEmpty()) currentPage + 1 else null
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private fun applyFilterAndSort(movies: List<MovieModel>): List<MovieModel> {
        // Horror Id -> 27
        // Comedy Id -> 35
        // Action Id -> 35
        return when (genreFilter) {
            GenreFilter.ALL -> sortMovies(movies)
            GenreFilter.HORROR -> sortMovies(movies.filter { it.genreIds.contains(27) })
            GenreFilter.COMEDY -> sortMovies(movies.filter { it.genreIds.contains(28) })
            GenreFilter.ACTION -> sortMovies(movies.filter { it.genreIds.contains(35) })
        }
    }

    private fun sortMovies(movies: List<MovieModel>): List<MovieModel> {
        return when (sortOption) {
            SortOption.DEFAULT -> movies // No sorting
            SortOption.POPULAR -> movies.sortedByDescending { it.popularity }
            SortOption.RATINGS -> movies.sortedByDescending { it.voteAverage }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

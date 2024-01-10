package com.example.popularmovies.ui.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.enums.GenreFilter
import com.example.popularmovies.data.enums.SortOption
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.model.Result
import com.example.popularmovies.data.repositories.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private var _moviesList = MutableLiveData<PagingData<MovieModel>>()
    val moviesList: LiveData<PagingData<MovieModel>> get() = _moviesList

    private val _movieDetail = MutableLiveData<MovieDetailModel?>()
    val movieDetail: LiveData<MovieDetailModel?> get() = _movieDetail

    var loading = MutableLiveData<Boolean>()

    private val _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?> get() = _error

    private var genre: GenreFilter = GenreFilter.ALL
    private var sort: SortOption = SortOption.DEFAULT

    fun getPopularMovies(context: Context) {
        viewModelScope.launch {
            postLoading(true)
            _moviesList =
                repository.getPopularMovies(context) as MutableLiveData<PagingData<MovieModel>>
        }
        postLoading(false)
    }

    fun getMovieDetails(movieId: Int, context: Context) {
        _movieDetail.postValue(null)
        viewModelScope.launch(Dispatchers.IO) {
            postLoading(true)
            val result = repository.getMovieDetails(movieId, Utility.API_KEY, context)
            when (result) {
                is Result.Success -> _movieDetail.postValue(result.data!!)
                is Result.Failure -> _error.postValue(result.exception)
            }
            postLoading(false)
        }
    }

    fun postLoading(boolean: Boolean) {
        loading.postValue(boolean)
    }


    fun applyFiltersAndSort(
        genreFilter: GenreFilter = genre,
        sortFilter: SortOption = sort,
        context: Context
    ) {
        genre = genreFilter
        sort = sortFilter
        repository.updateFilterAndSort(genreFilter, sortFilter, context)
    }
}

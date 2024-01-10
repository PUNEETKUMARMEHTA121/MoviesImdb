package com.example.popularmovies.ui.viewModels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.model.Result
import com.example.popularmovies.data.repositories.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
class MovieViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: MovieRepository

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var moviesObserver: Observer<PagingData<MovieModel>>

    @Mock
    private lateinit var movieDetailObserver: Observer<MovieDetailModel?>

    @Mock
    private lateinit var loadingObserver: Observer<Boolean>

    private lateinit var viewModel: MovieViewModel

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MovieViewModel(repository)
        viewModel.moviesList.observeForever(moviesObserver)
        viewModel.movieDetail.observeForever(movieDetailObserver)
        viewModel.loading.observeForever(loadingObserver)
    }

    @Test
    fun getPopularMoviesSuccess() = runBlockingTest {
        // Given
        val pagingData = PagingData.from(listOf(Mockito.mock(MovieModel::class.java)))
        val liveData = MutableLiveData(pagingData)
        `when`(repository.getPopularMovies(context)).thenReturn(liveData)

        // When
        viewModel.getPopularMovies(context)

        // Then
        // Use ArgumentCaptor to capture the argument passed to onChanged
        val captor = ArgumentCaptor.forClass(PagingData::class.java) as ArgumentCaptor<PagingData<MovieModel>>
        verify(moviesObserver).onChanged(captor.capture())

        // Assert the captured argument
        val capturedValue = captor.value
        assertNotNull(capturedValue) // Ensure that the captured value is not null
        assertEquals(pagingData, capturedValue)

        verify(loadingObserver).onChanged(false)
    }

    @Test
    fun getPopularMoviesError() {
        // Given
        val exception = Exception("Test error")
        `when`(repository.getPopularMovies(context)).thenAnswer {
            throw exception
        }

        // When
        viewModel.getPopularMovies(context)

        // Then
        verify(loadingObserver).onChanged(false)
        verifyZeroInteractions(moviesObserver)
        verify(loadingObserver).onChanged(false)
    }

    @Test
    fun getMovieDetailsSuccess() = runBlocking {
        // Given
        val movieId = 123
        val movieDetail = mock(MovieDetailModel::class.java)
        `when`(repository.getMovieDetails(movieId, Utility.API_KEY, context))
            .thenReturn(Result.Success(movieDetail))

        // When
        viewModel.getMovieDetails(movieId, context)

        // Then
        verify(movieDetailObserver).onChanged(movieDetail)
        verify(loadingObserver).onChanged(false)
    }


    @Test
    fun getMovieDetailsError() = runBlocking {
        // Given
        val movieId = 123
        val exception = Exception("Test error")
        `when`(repository.getMovieDetails(movieId, "API_KEY", context))
            .thenReturn(Result.Failure(exception))

        // When
        viewModel.getMovieDetails(movieId, context)

        // Then
        verify(movieDetailObserver).onChanged(null)
        verify(loadingObserver).onChanged(false)
    }

    @Test
    fun postLoading() {
        // When
        viewModel.postLoading(true)

        // Then
        verify(loadingObserver).onChanged(true)

        // When
        viewModel.postLoading(false)

        // Then
        verify(loadingObserver).onChanged(false)
    }
}
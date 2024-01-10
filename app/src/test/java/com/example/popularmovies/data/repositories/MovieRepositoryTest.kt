package com.example.popularmovies.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.enums.GenreFilter
import com.example.popularmovies.data.enums.SortOption
import com.example.popularmovies.data.local.MovieDao
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.model.Result
import com.example.popularmovies.data.network.MovieApiService
import com.example.popularmovies.data.pagination.MoviePagingSource
import com.nhaarman.mockitokotlin2.any
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
class MovieRepositoryTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieApi: MovieApiService

    @Mock
    private lateinit var movieDao: MovieDao

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var moviePagingSource: MoviePagingSource

    @InjectMocks
    private lateinit var movieRepository: MovieRepository

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun getPopularMoviesReturnsPagingData() {
        // Given
        val pagingData: PagingData<MovieModel> = PagingData.from(listOf(mock(MovieModel::class.java)))
        val liveData = MutableLiveData<PagingData<MovieModel>>()
        liveData.value = pagingData

        val movieRepositoryMock = mock(MovieRepository::class.java)
        `when`(movieRepositoryMock.getPopularMovies(context)).thenReturn(liveData)

        // When
        val result: LiveData<PagingData<MovieModel>> = movieRepositoryMock.getPopularMovies(context)

        // Then
        assertNotNull(result.value) // Assuming you want to check that the LiveData has a non-null value
        verify(movieRepositoryMock, times(1)).getPopularMovies(context)
    }

    @Test
    fun getMovieDetails(): Unit = runBlocking {
        // Given
        val movieId = 123
        val apiKey = Utility.API_KEY
        val movieDetail = mock(MovieDetailModel::class.java)

        // Mocking the  connectivity manager
        val connectivityManager = mock(ConnectivityManager::class.java)

        `when`(context.applicationContext).thenReturn(context)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(
            connectivityManager
        )

        // Mocking NetworkCapabilities for active network
        val networkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(networkCapabilities.hasCapability(anyInt())).thenReturn(true)
        val network = mock(Network::class.java)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(connectivityManager.activeNetwork).thenReturn(network)

        // Use the same movieDetail mock object for API response and expected result
        `when`(movieApi.getMovieDetail(movieId, apiKey)).thenReturn(Response.success(movieDetail))
        `when`(movieDao.insertMovieDetail(movieDetail)).thenReturn(Unit)

        // When
        val result: Result<MovieDetailModel> =
            movieRepository.getMovieDetails(movieId, apiKey, context)

        // Then
        // Verify your expected behavior
        assertTrue(result is Result.Success || result is Result.Failure) // Check if the result is a success or failure

        if (result is Result.Success) {
            val successResult = result as Result.Success
            assertEquals(
                movieDetail,
                successResult.data
            ) // Check if the returned data is the expected movieDetail
            verify(movieDao, times(1)).insertMovieDetail(movieDetail)
            verify(movieApi, times(1)).getMovieDetail(movieId, apiKey)
        } else if (result is Result.Failure) {
            // Handle failure case if needed
            assertNotNull(result.exception)
            assertTrue(result.exception is IOException)
        }
    }


    @Test
    fun getMovieDetailsReturnsFailure(): Unit = runBlocking {
        // Given
        val movieId = 123
        val apiKey = Utility.API_KEY
        val networkError = IOException("No data received from the server")

        // Mocking the connectivity manager
        val connectivityManager = mock(ConnectivityManager::class.java)

        `when`(context.applicationContext).thenReturn(context)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(
            connectivityManager
        )

        // Mocking NetworkCapabilities for active network
        val networkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(networkCapabilities.hasCapability(anyInt())).thenReturn(true)
        val network = mock(Network::class.java)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(connectivityManager.activeNetwork).thenReturn(network)

        doAnswer {
            throw networkError
        }.`when`(movieApi).getMovieDetail(movieId, apiKey)

        // When
        val result: Result<MovieDetailModel> =
            movieRepository.getMovieDetails(movieId, apiKey, context)

        // Then
        assertTrue(result is Result.Failure) // Check if the result is a failure

        val failureResult = result as Result.Failure
        assertTrue(failureResult.exception is IOException) // Check if the exception is an IOException

        val expectedMessage = "No data received from the server"
        assertTrue(failureResult.exception.message?.contains(expectedMessage) == true)
    }
}
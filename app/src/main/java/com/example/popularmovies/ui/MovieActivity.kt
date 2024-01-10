package com.example.popularmovies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.popularmovies.R
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.local.MovieDatabase
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.data.network.MoviesApiServiceInstance
import com.example.popularmovies.data.repositories.MovieRepository
import com.example.popularmovies.databinding.ActivityMovieBinding
import com.example.popularmovies.ui.movieDetailScreen.MovieDetailFragment
import com.example.popularmovies.ui.moviesListScreen.MovieGridFragment
import com.example.popularmovies.ui.viewModelFactories.MovieViewModelFactory
import com.example.popularmovies.ui.viewModels.MovieViewModel

class MovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieBinding
    lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie)

        val movieApiService = MoviesApiServiceInstance.apiService
        val movieDao = MovieDatabase.getInstance(applicationContext).movieDao()
        val repository = MovieRepository(movieApiService, movieDao)
        val factory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]

        // Load the initial fragment (e.g., MovieListFragment)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MovieGridFragment())
                .commit()
        }
    }

    fun navigateToMovieDetail(movie: MovieModel) {
        val movieDetailFragment = MovieDetailFragment()
        val bundle = Bundle().apply {
            putInt(Utility.movieId, movie.id)
        }
        movieDetailFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, movieDetailFragment)
            .addToBackStack(null) // Optional: Add the transaction to the back stack
            .commit()
    }
}


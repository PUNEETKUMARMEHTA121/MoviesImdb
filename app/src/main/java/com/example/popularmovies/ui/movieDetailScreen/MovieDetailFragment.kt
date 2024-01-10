package com.example.popularmovies.ui.movieDetailScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.popularmovies.R
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.interfaces.IViewClicked
import com.example.popularmovies.data.model.MovieDetailModel
import com.example.popularmovies.databinding.FragmentMovieDetailBinding
import com.example.popularmovies.ui.MovieActivity

class MovieDetailFragment : Fragment() {

    private lateinit var binding: FragmentMovieDetailBinding
    private var movieId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movie_detail, container, false
        )
        movieId = arguments?.getInt(Utility.movieId) ?: 0

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MovieActivity).movieViewModel.getMovieDetails(
            movieId,
            requireContext()
        )

        binding.retry = object : IViewClicked {
            override fun onClick() {
                binding.error = false
                (requireActivity() as MovieActivity).movieViewModel.getMovieDetails(
                    movieId,
                    requireContext()
                )
            }
        }

        (requireActivity() as MovieActivity).movieViewModel.movieDetail.observe(
            viewLifecycleOwner
        ) { movieDetails ->
            movieDetails?.let {
                updateUi(it)
            }
        }

        (requireActivity() as MovieActivity).movieViewModel.loading.observe(this) {
            binding.loading = it
        }

        (requireActivity() as MovieActivity).movieViewModel.error.observe(this) {
            binding.error = true
            Toast.makeText(requireContext(), "Error Msg : $it", Toast.LENGTH_LONG).show()
        }

    }

    private fun updateUi(movieDetailModel: MovieDetailModel) {
        movieDetailModel.apply {
            binding.movieDetail = this
            binding.genreString = generateGenreString()
            binding.spokenLanguagesString = generateSpokenLanguagesString()
            binding.productionCountriesString = generateProductionCountriesString()
            if (posterPath.isNullOrBlank().not()) {
                Glide.with(requireContext())
                    .load(Utility.IMAGE_BASE_URL + posterPath)
                    .placeholder(0)
                    .error(R.drawable.error_loading_image)
                    .into(binding.imageMovie)
            }
        }
    }
}
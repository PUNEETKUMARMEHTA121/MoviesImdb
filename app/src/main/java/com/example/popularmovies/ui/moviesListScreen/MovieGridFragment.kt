package com.example.popularmovies.ui.moviesListScreen

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.popularmovies.R
import com.example.popularmovies.data.enums.GenreFilter
import com.example.popularmovies.data.enums.SortOption
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.databinding.FragmentMovieGridBinding
import com.example.popularmovies.ui.MovieActivity
import kotlinx.coroutines.launch

class MovieGridFragment : Fragment() {


    private lateinit var binding: FragmentMovieGridBinding // Generated binding class

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_movie_grid, container, false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val movieGridAdapter = MovieGridAdapter { selectedMovie ->
            // Handle click event, i.e. , navigate to detail screen
            navigateToDetailScreen(selectedMovie)
        }
        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = movieGridAdapter
        }
        (requireActivity() as MovieActivity).movieViewModel.getPopularMovies(requireContext())

        // Observe LiveData from ViewModel
        (requireActivity() as MovieActivity).movieViewModel.moviesList.observe(
            viewLifecycleOwner
        ) { pagingData ->
            viewLifecycleOwner.lifecycleScope.launch {
                movieGridAdapter.submitData(pagingData)
            }
        }

        (requireActivity() as MovieActivity).movieViewModel.loading.observe(this) {
            binding.loading = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter_horror -> handleFilter(GenreFilter.HORROR)
            R.id.filter_comedy -> handleFilter(GenreFilter.COMEDY)
            R.id.filter_action -> handleFilter(GenreFilter.ACTION)
            R.id.sort_popular -> handleSort(SortOption.POPULAR)
            R.id.sort_ratings -> handleSort(SortOption.RATINGS)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun handleFilter(filterType: GenreFilter) {
        (activity as MovieActivity).movieViewModel.applyFiltersAndSort(
            genreFilter = filterType,
            context = requireContext()
        )
    }

    private fun handleSort(sortType: SortOption) {
        (activity as MovieActivity).movieViewModel.applyFiltersAndSort(
            sortFilter = sortType,
            context = requireContext()
        )
    }

    private fun navigateToDetailScreen(selectedMovie: MovieModel) {
        // Implement Navigation Logic
        (requireActivity() as MovieActivity).navigateToMovieDetail(selectedMovie)
    }
}

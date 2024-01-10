package com.example.popularmovies.ui.moviesListScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.popularmovies.R
import com.example.popularmovies.data.Utility
import com.example.popularmovies.data.model.MovieModel
import com.example.popularmovies.databinding.ItemMovieGridBinding

class MovieGridAdapter(private val onItemClick: (MovieModel) -> Unit) :
    PagingDataAdapter<MovieModel, MovieGridAdapter.MovieViewHolder>(MovieModelDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding =
            ItemMovieGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class MovieViewHolder(private val binding: ItemMovieGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                getItem(absoluteAdapterPosition)?.let { onItemClick.invoke(it) }
            }
        }

        fun bind(movie: MovieModel) {
            binding.movie = movie
            if (movie.posterPath.isNullOrBlank().not()) {

                Glide.with(itemView.context)
                    .load(Utility.IMAGE_BASE_URL + movie.posterPath)
                    .placeholder(0)
                    .error(R.drawable.error_loading_image)
                    .into(binding.moviePoster)
            }
            binding.executePendingBindings()
        }
    }

    companion object {
        private val MovieModelDiffCallback = object : DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

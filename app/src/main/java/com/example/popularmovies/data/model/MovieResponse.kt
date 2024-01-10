package com.example.popularmovies.data.model

data class MovieResponse(
    val page: Int,
    val results: List<MovieModel>,
    val total_pages: Int,
    val total_results: Int
) {
    companion object {
        fun empty() = MovieResponse(-1, listOf(), -1, 0)
    }
}

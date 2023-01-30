package com.example.imdbapp.presentation.moviedetails

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imdbapp.core.util.Resource
import com.example.imdbapp.domain.usecase.getmoviedetails.GetMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {
    private var _state = mutableStateOf(MovieDetailsState())
    val state: State<MovieDetailsState> = _state

    init {
        getMovieDetails()
    }

    private fun getMovieDetails() {
        viewModelScope.launch {
            Log.d("MovieDetailsViewModel", "${savedStateHandle.get<String>("id") ?: null}")
            val id = savedStateHandle.get<String>("id") ?: return@launch
            getMovieDetailsUseCase.getMovieDetails(id).collect() { results ->
                when (results) {
                    is Resource.Success -> {
                        _state.value = MovieDetailsState(movieDetails = results.data)
                    }
                    is Resource.Error -> {
                        _state.value =
                            MovieDetailsState(error = results.message ?: "Unexpected error!")
                    }
                    is Resource.Loading -> {
                        _state.value = MovieDetailsState(isLoading = true)
                    }
                }
            }
        }
    }
}
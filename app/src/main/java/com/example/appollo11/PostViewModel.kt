package com.example.appollo11

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val posts: List<Post>) : UiState()
    data class Error(val message: String) : UiState()
}

class PostViewModel @JvmOverloads constructor(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = repository.fetchPosts()) {
                is RepositoryResult.Success -> _uiState.value = UiState.Success(result.posts)
                is RepositoryResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun retry() {
        loadPosts()
    }
}

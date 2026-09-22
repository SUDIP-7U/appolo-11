package com.example.appollo11

import retrofit2.HttpException
import java.io.IOException

/**
 * Result wrapper so the repository can hand the ViewModel a clean
 * success/failure outcome without leaking exception types upward.
 */
sealed class RepositoryResult {
    data class Success(val posts: List<Post>) : RepositoryResult()
    data class Error(val message: String) : RepositoryResult()
}

class PostRepository(
    private val api: ApiService = RetrofitInstance.api
) {

    suspend fun fetchPosts(): RepositoryResult {
        return try {
            val posts = api.getPosts()
            RepositoryResult.Success(posts)
        } catch (e: IOException) {
            // No internet connection / network failure
            RepositoryResult.Error("No internet connection. Please check your network and try again.")
        } catch (e: HttpException) {
            // Server returned a non-2xx response
            RepositoryResult.Error("Server error (${e.code()}). Please try again later.")
        } catch (e: Exception) {
            // Catch-all for anything unexpected (e.g. JSON parsing issues)
            RepositoryResult.Error("Something went wrong: ${e.localizedMessage ?: "unknown error"}")
        }
    }
}

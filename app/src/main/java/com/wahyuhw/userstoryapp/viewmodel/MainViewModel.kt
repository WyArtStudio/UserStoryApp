package com.wahyuhw.userstoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wahyuhw.userstoryapp.data.params.AddStoryParameter
import com.wahyuhw.userstoryapp.data.repository.MainRepository
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.data.room.BookmarkStoryEntity
import com.wahyuhw.userstoryapp.data.room.UserEntity
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    val story: LiveData<PagingData<StoryItem>> =
        repository.getListPagingStory().cachedIn(viewModelScope)

    fun login(email: String, password: String) = repository.login(email, password)
    
    fun register(username: String, email: String, password: String) = repository.register(username, email, password)

    fun insertBookmarkStory(story: BookmarkStoryEntity) = viewModelScope.launch {
        repository.insertBookmarkStory(story)
    }

    fun deleteBookmarkStory(story: BookmarkStoryEntity) = viewModelScope.launch {
        repository.deleteBookmarkStory(story)
    }

    suspend fun getListLocatedStory() = repository.getListLocatedStory()

    suspend fun addStory(imageMultipart: MultipartBody.Part, addStoryParams: AddStoryParameter) =
        repository.addStory(imageMultipart, addStoryParams)

    fun getToken() = repository.getToken()

    fun saveToken(token: String) = viewModelScope.launch { repository.saveToken(token) }

    fun insertUser(user: UserEntity) = viewModelScope.launch { repository.insertUser(user) }

    fun clearUser() = viewModelScope.launch { repository.clearUser() }

    fun saveSession(isAlreadyLogged: Boolean) = viewModelScope.launch {
        repository.saveSession(isAlreadyLogged)
    }

    fun getSession() = repository.getSession()

    suspend fun getUser() = repository.getUser()

    suspend fun getDetailStory(name: String) = repository.getDetailStory(name)

    suspend fun getListBookmarkStory() = repository.getListBookmarkStory()

    suspend fun clearBookmarkStory() = repository.clearBookmarkStory()
}
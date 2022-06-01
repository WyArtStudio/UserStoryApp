package com.wahyuhw.userstoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.wahyuhw.userstoryapp.data.network.ApiInterface
import com.wahyuhw.userstoryapp.data.network.ResponseResource
import com.wahyuhw.userstoryapp.data.params.AddStoryParameter
import com.wahyuhw.userstoryapp.data.params.LoginParameter
import com.wahyuhw.userstoryapp.data.params.RegisterParameter
import com.wahyuhw.userstoryapp.data.prefs.SettingsPreferences
import com.wahyuhw.userstoryapp.data.remote.StoryRemoteMediator
import com.wahyuhw.userstoryapp.data.response.ApiResponse
import com.wahyuhw.userstoryapp.data.response.LoginResponse
import com.wahyuhw.userstoryapp.data.response.StoryItem
import com.wahyuhw.userstoryapp.data.response.StoryResponse
import com.wahyuhw.userstoryapp.data.room.*
import com.wahyuhw.userstoryapp.ui.activity.DetailActivity
import com.wahyuhw.userstoryapp.utils.addBearerToken
import com.wahyuhw.userstoryapp.utils.map
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(private val database: StoryDatabase, private val retrofit: ApiInterface, private val prefs: SettingsPreferences) {
    private val bookmarkStoryDao: BookmarkStoryDao = database.bookmarkStoryDao()
    private val userDao: UserDao = database.userDao()

    fun login(email: String, password: String): LiveData<ResponseResource<LoginResponse>> {
        val loginResponse = MutableLiveData<ResponseResource<LoginResponse>>()
        loginResponse.postValue(ResponseResource.Loading())

        val loginParams = LoginParameter(email, password)
        retrofit.loginUser(loginParams.map()).enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val user = response.body()
                loginResponse.postValue(ResponseResource.Success(user))
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResponse.postValue(ResponseResource.Error(t.message))
            }

        })

        return loginResponse
    }

    suspend fun addStory(imageMultipart: MultipartBody.Part, addStoryParams: AddStoryParameter): LiveData<ResponseResource<ApiResponse>> {
        val apiResponse = MutableLiveData<ResponseResource<ApiResponse>>()
        apiResponse.postValue(ResponseResource.Loading())
        val token = getToken().first()?.addBearerToken()

        retrofit.addStory(token!!, addStoryParams.map(), imageMultipart).enqueue(object: Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val message = response.body()
                apiResponse.postValue(ResponseResource.Success(message))
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                apiResponse.postValue(ResponseResource.Error(t.message))
            }
        })

        return apiResponse
    }

    fun register(username: String, email: String, password: String): LiveData<ResponseResource<ApiResponse>> {
        val user = MutableLiveData<ResponseResource<ApiResponse>>()
        user.postValue(ResponseResource.Loading())

        val registerParams = RegisterParameter(username, email, password)
        retrofit.registerUser(registerParams.map()).enqueue(object: Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val register = response.body()
                user.postValue(ResponseResource.Success(register))
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                user.postValue(ResponseResource.Error(t.message))
            }

        })

        return user
    }

    @OptIn(ExperimentalPagingApi::class)
    suspend fun getListPagedStory(): LiveData<PagingData<StoryItem>> {
        val token = getToken().first()?.addBearerToken()
        return Pager(
            config = PagingConfig(
                pageSize = 5),
            remoteMediator = StoryRemoteMediator(database, retrofit, token!!),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }).liveData
    }

    suspend fun getListLocatedStory(): LiveData<ResponseResource<StoryResponse>> {
        val listStory = MutableLiveData<ResponseResource<StoryResponse>>()
        listStory.postValue(ResponseResource.Loading())
        val token = getToken().first()
        retrofit.getLocatedStory(token!!.addBearerToken()).enqueue(object: Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                val data = response.body()
                listStory.postValue(ResponseResource.Success(data))
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                listStory.postValue(ResponseResource.Error(t.message))
            }

        })

        return listStory
    }

    suspend fun getListBookmarkStory(): LiveData<ResponseResource<List<BookmarkStoryEntity>>> {
        val listStory = MutableLiveData<ResponseResource<List<BookmarkStoryEntity>>>()
        listStory.postValue(ResponseResource.Loading())

        if (bookmarkStoryDao.getListBookmarkStory().isEmpty()) {
            listStory.postValue(ResponseResource.Error(null))
        } else {
            listStory.postValue(ResponseResource.Success(bookmarkStoryDao.getListBookmarkStory()))
        }

        return listStory
    }

    suspend fun getDetailStory(name: String): LiveData<ResponseResource<BookmarkStoryEntity>> {
        val story = MutableLiveData<ResponseResource<BookmarkStoryEntity>>()
        story.postValue(ResponseResource.Loading())

        if (bookmarkStoryDao.getDetailStory(name) != null) {
            story.postValue(ResponseResource.Success(bookmarkStoryDao.getDetailStory(name)))
        } else {
            story.postValue(ResponseResource.Error(DetailActivity.EXTRA_STORY))
        }

        return story
    }

    fun getToken() = prefs.getTokenSetting()

    suspend fun saveToken(token: String) {
        prefs.saveTokenSetting(token)
    }

    fun getSession() = prefs.getSessionSetting()

    suspend fun saveSession(isAlreadyLogged: Boolean) {
        prefs.saveSessionSetting(isAlreadyLogged)
    }

    suspend fun insertBookmarkStory(story: BookmarkStoryEntity) = bookmarkStoryDao.insert(story)

    suspend fun deleteBookmarkStory(story: BookmarkStoryEntity) = bookmarkStoryDao.delete(story)

    suspend fun clearBookmarkStory() = bookmarkStoryDao.clearStory()

    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    suspend fun getUser(): LiveData<ResponseResource<UserEntity>> {
        val user = MutableLiveData<ResponseResource<UserEntity>>()
        user.postValue(ResponseResource.Loading())

        if (userDao.getUser() != null) {
            user.postValue(ResponseResource.Success(userDao.getUser()))
        } else {
            user.postValue(ResponseResource.Error("No user!"))
        }

        return user
    }

    suspend fun clearUser() = userDao.clearUser()
}
package com.wahyuhw.userstoryapp.data.network

sealed class ResponseResource<T>(val data: T?  = null, val message: String? = null) {
    class Success<T>(data: T?): ResponseResource<T>(data)
    class Error<T>(message: String?, data: T? = null): ResponseResource<T>(data, message)
    class Loading<T>(data: T? = null): ResponseResource<T>(data)
}

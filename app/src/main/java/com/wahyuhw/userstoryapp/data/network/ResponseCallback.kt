package com.wahyuhw.userstoryapp.data.network

import android.view.View

interface ResponseCallback<T> {
    fun onSuccess(data: T)
    fun onLoading()
    fun onFailed(message: String?)

    val visible: Int
        get() = View.VISIBLE

    val gone: Int
        get() = View.GONE
}
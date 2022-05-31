package com.wahyuhw.userstoryapp.data.params

import java.io.File

data class AddStoryParameter(
    val description: String,
    val photo: File,
    val lat: Float?,
    val lon: Float?)
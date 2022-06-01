package com.wahyuhw.userstoryapp.data.params

import android.location.Location
import java.io.File

data class AddStoryParameter(
    val description: String,
    val photo: File,
    val location: Location? = null
)
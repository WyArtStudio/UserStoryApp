package com.wahyuhw.userstoryapp.data.response

import com.google.gson.annotations.SerializedName

data class ApiResponse(
	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

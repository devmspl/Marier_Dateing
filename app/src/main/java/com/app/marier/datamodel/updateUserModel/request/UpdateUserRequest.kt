package com.app.marier.datamodel.updateUserModel.request

data class UpdateUserRequest(
    val name: String,
    val phoneNumber: String,
    val dob: String,
    val email: String,
    val bio: String,
    val sex: String,
    val address: String,
)

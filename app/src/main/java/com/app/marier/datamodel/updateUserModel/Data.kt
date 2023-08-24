package com.app.marier.datamodel.updateUserModel

data class Data(
    val _id: String,
    val address: String,
    val avatar: String,
    val bio: String,
    val createdAt: String,
    val dob: String,
    val email: String,
    val fcmToken: String,
    val gallery: List<Gallery>,
    val interests: List<String>,
    val ipAddress: String,
    val is_deleted: Boolean,
    val lastActive: String,
    val name: String,
    val phoneNumber: String,
    val platform: String,
    val profession: String,
    val setting: Setting,
    val sex: String,
    val sexualOrientations: List<Any>,
    val socialLogin: SocialLogin,
    val status: String,
    val token: String,
    val updatedAt: String,
    val uuid: String
)
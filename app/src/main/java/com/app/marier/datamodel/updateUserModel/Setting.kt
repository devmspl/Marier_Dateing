package com.app.marier.datamodel.updateUserModel

data class Setting(
    val ageRange: AgeRange,
    val distance: Int,
    val language: String,
    val location: Location,
    val sexType: String
)
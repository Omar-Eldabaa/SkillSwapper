package com.example.skillswapper.model

data class UserSkillsSetup(
    val userId: String? = null,
    val knownSkills: List<String> = listOf(),
    val knownCategory: String? = null,
    val desiredSkills: List<String> = listOf(),
    val desiredCategory: String? = null,
    val preferredLanguage: String? = null,
    val bio: String? = null
)
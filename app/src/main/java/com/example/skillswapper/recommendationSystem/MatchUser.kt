package com.example.skillswapper.recommendationSystem

import com.example.skillswapper.model.UserSkillsSetup

enum class MatchType {
    PERFECT, GOOD, POTENTIAL
}

data class MatchingUser(
    val userSkills: UserSkillsSetup,
    val userName: String,
    val matchStrength: Int,
    val matchType: MatchType
)

//package com.example.skillswapper.recommendationSystem
//
//import com.example.skillswapper.model.UserSkillsSetup
//
//object RecommendationSystem {
//
//    // حساب التوافق بين المهارات
//    private fun calculateCompatibility(currentUserSkills: UserSkillsSetup, otherUserSkills: UserSkillsSetup): Int {
//        var score = 0
//
//        // تطابق المهارات المعروفة
//        score += currentUserSkills.knownSkills.intersect(otherUserSkills.knownSkills.toSet()).size * 10
//
//        // تطابق المهارات المرغوبة
//        score += currentUserSkills.desiredSkills.intersect(otherUserSkills.knownSkills.toSet()).size * 10
//
//        // تطابق الفئات
//        if (currentUserSkills.knownCategory == otherUserSkills.knownCategory) {
//            score += 20
//        }
//
//        // تطابق المهارات المرغوبة مع المهارات التي يعرفها المستخدم الآخر
//        score += currentUserSkills.desiredSkills.intersect(otherUserSkills.desiredSkills.toSet()).size * 10
//
//        return score
//    }
//
//    // دالة لتصفية المستخدمين بناءً على تطابق المهارات
//    fun getMatchingUsers(currentUserSkills: UserSkillsSetup, allUsers: List<UserSkillsSetup>, allUserNames: Map<String?, String>): List<MatchingUser> {
//        val matchingUsers = mutableListOf<MatchingUser>()
//
//        // البحث عن تطابقات مباشرة (match)
//        for (otherUser in allUsers) {
//            if (otherUser.userId != currentUserSkills.userId) {
//                val matchStrength = calculateCompatibility(currentUserSkills, otherUser)
//
//                if (matchStrength > 0) {
//                    val userName = allUserNames[otherUser.userId] ?: "Unknown"  // استرجاع الاسم من الخريطة أو وضع اسم افتراضي
//                    matchingUsers.add(MatchingUser(otherUser, userName, matchStrength))
//                }
//            }
//        }
//
//        // تحديد أعلى درجة تطابق (maxStrength)
//        val maxStrength = 100  // أعلى درجة تطابق متوقعة بناءً على حسابات التوافق
//
//        // تصنيف الأشخاص إلى تطابقات كاملة وجزئية
//        val fullMatches = matchingUsers.filter { it.matchStrength == maxStrength }
//        val partialMatches = matchingUsers.filter { it.matchStrength < maxStrength }
//
//        // ترتيب النتائج حسب قوة التطابق، العرض الأول للتطابقات الكاملة، ثم الجزئية
//        return fullMatches + partialMatches
//    }
//}

//package com.example.skillswapper.recommendationSystem
//
//import com.example.skillswapper.model.UserSkillsSetup
//
//object RecommendationSystem {
//
//    private fun calculateCompatibility(currentUserSkills: UserSkillsSetup, otherUserSkills: UserSkillsSetup): Int {
//        var score = 0
//
//        val currentKnown = currentUserSkills.knownSkills.toSet()
//        val currentDesired = currentUserSkills.desiredSkills.toSet()
//        val otherKnown = otherUserSkills.knownSkills.toSet()
//        val otherDesired = otherUserSkills.desiredSkills.toSet()
//
//        // المهارات المشتركة المعروفة
//        score += (currentKnown intersect otherKnown).size * 10
//
//        // المهارات اللي المستخدم عايز يتعلمها واللي الطرف الآخر يعرفها
//        score += (currentDesired intersect otherKnown).size * 15  // وزن أعلى
//
//        // المهارات اللي الطرف الآخر عايز يتعلمها واللي المستخدم يعرفها
//        score += (currentKnown intersect otherDesired).size * 10
//
//        // المهارات المرغوبة المشتركة
//        score += (currentDesired intersect otherDesired).size * 5
//
//        // تطابق الفئة
//        if (currentUserSkills.knownCategory == otherUserSkills.knownCategory) {
//            score += 20
//        }
//
//        return score
//    }
//
//    fun getMatchingUsers(
//        currentUserSkills: UserSkillsSetup,
//        allUsers: List<UserSkillsSetup>,
//        allUserNames: Map<String?, String>
//    ): List<MatchingUser> {
//        val matchingUsers = mutableListOf<MatchingUser>()
//
//        for (otherUser in allUsers) {
//            if (otherUser.userId != currentUserSkills.userId) {
//                val matchStrength = calculateCompatibility(currentUserSkills, otherUser)
//
//                if (matchStrength > 0) {
//                    val userName = allUserNames[otherUser.userId] ?: "Unknown"
//                    matchingUsers.add(MatchingUser(otherUser, userName, matchStrength))
//                }
//            }
//        }
//
//        // ترتيب النتائج حسب قوة التطابق تنازليًا
//        return matchingUsers.sortedByDescending { it.matchStrength }
//    }
//}

package com.example.skillswapper.recommendationSystem

import android.util.Log
import com.example.skillswapper.model.UserSkillsSetup

object RecommendationSystem {

    // حساب التوافق بناءً على التبادل الفعلي والاهتمامات
    private fun calculateCompatibility(currentUserSkills: UserSkillsSetup, otherUserSkills: UserSkillsSetup): Int {
        var score = 0

        // المستخدم الحالي عايز يتعلم مهارات المستخدم الآخر يعرفها (تبادل حقيقي)
        val teachableMatch = currentUserSkills.desiredSkills
            .intersect(otherUserSkills.knownSkills.toSet()).size * 25 // زيادة الوزن هنا

        // المستخدم الحالي يعرف مهارات المستخدم الآخر عايز يتعلمها (تبادل بالعكس)
        val learnableMatch = currentUserSkills.knownSkills
            .intersect(otherUserSkills.desiredSkills.toSet()).size * 25 // زيادة الوزن هنا

        // اهتمام مشترك في مهارات مرغوبة (ممكن إضافة وزن أكبر)
        val sharedInterest = currentUserSkills.desiredSkills
            .intersect(otherUserSkills.desiredSkills.toSet()).size * 10 // زيادة الوزن هنا

        // نفس الكاتيجوري للمهارات (تعزيز التوافق إذا كانت الفئات متطابقة)
        val categoryBonus = if (currentUserSkills.knownCategory == otherUserSkills.knownCategory) 20 else 0 // زيادة الوزن هنا

        // مجموع السكور
        score += teachableMatch + learnableMatch + sharedInterest + categoryBonus

        Log.d("RecommendationSystem", "Matching score between ${currentUserSkills.userId} and ${otherUserSkills.userId}: $score")

        return score
    }

    // تحديد نوع الماتش بناءً على السكور
    private fun classifyMatch(score: Int): MatchType {
        return when {
            score >= 100 -> MatchType.PERFECT  // تعديل العتبة لتكون أكثر دقة
            score >= 60 -> MatchType.GOOD      // تعديل العتبة لتكون أكثر دقة
            else -> MatchType.POTENTIAL
        }
    }

    // الحصول على المستخدمين المناسبين
    fun getMatchingUsers(
        currentUserSkills: UserSkillsSetup,
        allUsers: List<UserSkillsSetup>,
        allUserNames: Map<String?, String>
    ): List<MatchingUser> {
        val matchingUsers = mutableListOf<MatchingUser>()

        for (otherUser in allUsers) {
            if (otherUser.userId != currentUserSkills.userId) {
                val matchStrength = calculateCompatibility(currentUserSkills, otherUser)

                if (matchStrength > 0) {
                    val userName = allUserNames[otherUser.userId] ?: "Unknown"
                    val matchType = classifyMatch(matchStrength)

                    matchingUsers.add(
                        MatchingUser(otherUser, userName, matchStrength, matchType)
                    )
                }
            }
        }

        return matchingUsers.sortedByDescending { it.matchStrength }
    }
}



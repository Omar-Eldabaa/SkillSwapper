package com.example.skillswapper.matching

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skillswapper.R
import com.example.skillswapper.databinding.ItemUserMatchBinding
import com.example.skillswapper.recommendationSystem.MatchType
import com.example.skillswapper.recommendationSystem.MatchingUser
import com.google.android.material.chip.Chip

class MatchingUsersAdapter : ListAdapter<MatchingUser, MatchingUsersAdapter.RecommendationViewHolder>(
    DiffCallback()
) {
        lateinit var listener: OnProfileClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val binding = ItemUserMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class RecommendationViewHolder(private val binding: ItemUserMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: MatchingUser) {
            // تعيين بيانات المستخدم
            binding.userName.text = user.userName
            binding.matchStrengthBar.progress = user.matchStrength

            // عرض نوع الماتش
            binding.matchTypeChip.apply {
                text = when (user.matchType) {
                    MatchType.PERFECT -> "Perfect Match"
                    MatchType.GOOD -> "Good Match"
                    MatchType.POTENTIAL -> "Potential Match"
                }

                // تغيير اللون حسب النوع
                chipBackgroundColor = when (user.matchType) {
                    MatchType.PERFECT -> context.getColorStateList(R.color.green)
                    MatchType.GOOD -> context.getColorStateList(R.color.orange)
                    MatchType.POTENTIAL -> context.getColorStateList(R.color.gray)
                }
            }


            binding.cardView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> v.elevation = 12f
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.elevation = 6f
                }
                false
            }



            // عرض المهارات المتوافقة
            binding.matchedSkillsGroup.removeAllViews()
            for (skill in user.userSkills.knownSkills) {
                val chip = Chip(binding.root.context)
                chip.text = skill
                chip.isClickable = false
                binding.matchedSkillsGroup.addView(chip)
            }

            // عرض صورة المستخدم (افتراضياً placeholder)
            binding.profileImage.setImageResource(R.drawable.ic_user_placeholder)
            binding.sendMessageButton.setOnClickListener {
                listener.onSendMessageClick(user)
            }
            binding.viewProfileButton.setOnClickListener {
                    listener.onViewProfileClick(user.userSkills.userId?:"")
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MatchingUser>() {
        override fun areItemsTheSame(oldItem: MatchingUser, newItem: MatchingUser): Boolean {
            return oldItem.userSkills.userId == newItem.userSkills.userId
        }

        override fun areContentsTheSame(oldItem: MatchingUser, newItem: MatchingUser): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnProfileClickListener {
    fun onViewProfileClick(userId: String)
    fun onSendMessageClick(user: MatchingUser)
}


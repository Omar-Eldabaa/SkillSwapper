package com.example.skillswapper.sessions

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillswapper.SessionProvider
import com.example.skillswapper.databinding.ItemScheduledSessionBinding
import com.example.skillswapper.model.SessionWithDetails
import com.example.skillswapper.profileActivity.ProfileActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.*

class ScheduledSessionsAdapter(
    private var sessions: List<SessionWithDetails>
) : RecyclerView.Adapter<ScheduledSessionsAdapter.SessionViewHolder>() {

    inner class SessionViewHolder(val binding: ItemScheduledSessionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemScheduledSessionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val sessionWithDetails = sessions[position]
        val context = holder.itemView.context
        val binding = holder.binding

        val session = sessionWithDetails.session
        val senderName = sessionWithDetails.senderName
        val skillName = sessionWithDetails.skillName

        binding.tvUserName.text = senderName
        binding.tvSkill.text = "Skill: $skillName"

        val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(session.scheduledTime))
        binding.tvSessionTime.text = "Scheduled: $formattedDate"



        holder.itemView.setOnClickListener {
//            val currentUserId =Firebase.auth.currentUser?.uid
            val currentUserId =SessionProvider.user?.id

            val otherUserId = if (session.senderId == currentUserId) session.receiverId else session.senderId

            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("userId", otherUserId)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = sessions.size

    fun updateData(newSessions: List<SessionWithDetails>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}

package com.example.skillswapper.sessions

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillswapper.databinding.ItemIncomingSessionBinding
import com.example.skillswapper.model.SessionWithDetails
import com.example.skillswapper.profileActivity.ProfileActivity
import java.text.SimpleDateFormat
import java.util.*

class IncomingSessionsAdapter(
    private val sessions: List<SessionWithDetails>,
    private val onAcceptClicked: (SessionWithDetails) -> Unit,
    private val onRejectClicked: (SessionWithDetails) -> Unit
) : RecyclerView.Adapter<IncomingSessionsAdapter.SessionViewHolder>() {

    inner class SessionViewHolder(val binding: ItemIncomingSessionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemIncomingSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessionViewHolder(binding)


    }

    override fun getItemCount() = sessions.size

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val item = sessions[position]
        val session = item.session
        val binding = holder.binding

        binding.tvRequesterName.text = item.senderName
        binding.tvSkillName.text = "Skill: ${item.skillName}"

        val formattedTime = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            .format(Date(session.scheduledTime))
        binding.tvSessionTime.text = "Scheduled: $formattedTime"

        binding.btnAccept.setOnClickListener { onAcceptClicked(item) }
        binding.btnReject.setOnClickListener { onRejectClicked(item) }



        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("userId", session.senderId)
            context.startActivity(intent)
        }

    }
}

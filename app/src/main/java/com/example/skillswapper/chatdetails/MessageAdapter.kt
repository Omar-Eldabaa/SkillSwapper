package com.example.skillswapper.chatdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skillswapper.R
import com.example.skillswapper.databinding.ItemMessageBinding
import com.example.skillswapper.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val chatList: List<Message>,
    private val currentUserId: String,
    private val otherUserName: String

) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = chatList[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = chatList.size

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            val isMine = message.senderId == currentUserId

            // اسم المرسل
            binding.textSenderName.text = if (isMine) "You" else otherUserName

            // نص الرسالة
            binding.textMessage.text = message.content

            // الوقت
            val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
            binding.textTimestamp.text = formattedTime

            // تغيير لون البالونة
            val context = binding.root.context
            val backgroundRes = if (isMine) R.drawable.bg_message_mine else R.drawable.bg_message_other
            binding.messageBubble.setBackgroundResource(backgroundRes)

            val textColor = if (isMine) R.color.white else R.color.black
            binding.textMessage.setTextColor(ContextCompat.getColor(context, textColor))

            // تغيير المحاذاة
            val gravity = if (isMine) Gravity.END else Gravity.START

            // تطبيق المحاذاة على كل العناصر
            (binding.textSenderName.layoutParams as LinearLayout.LayoutParams).gravity = gravity
            (binding.messageBubble.layoutParams as LinearLayout.LayoutParams).gravity = gravity
            (binding.textTimestamp.layoutParams as LinearLayout.LayoutParams).gravity = gravity
        }
    }
}

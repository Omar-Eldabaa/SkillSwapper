package com.example.skillswapper.matching

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skillswapper.databinding.BottomSheetSendMessageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SendMessageBottomSheet(
    private val receiverId: String,
    private val receiverName: String,
    private val onSendClick: (String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSendMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSendMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.receiverNameText.text = "Send a message to $receiverName"

        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                onSendClick(message)
                dismiss()
            } else {
                binding.messageInput.error = "Message cannot be empty"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val halfScreenHeight = screenHeight / 2

            it.layoutParams.height = halfScreenHeight
            it.requestLayout()

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = halfScreenHeight
        }
    }

}

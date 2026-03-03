package com.matanh.transfer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.matanh.transfer.R
import com.matanh.transfer.server.ChatRepository
import kotlinx.coroutines.launch

class ChatBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var rvChatMessages: RecyclerView
    private lateinit var etChatMessage: EditText
    private lateinit var btnSendChat: FloatingActionButton
    private lateinit var btnDeleteAllMessages: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_bottom_sheet, container, false)
        rvChatMessages = view.findViewById(R.id.rvChatMessages)
        etChatMessage = view.findViewById(R.id.etChatMessage)
        btnSendChat = view.findViewById(R.id.btnSendChat)
        btnDeleteAllMessages = view.findViewById(R.id.btnDeleteAllMessages)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter { id ->
            ChatRepository.deleteMessageById(id)
        }
        rvChatMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        rvChatMessages.adapter = chatAdapter

        // Load initial messages
        val initialMessages = ChatRepository.messages
        chatAdapter.submitList(initialMessages)
        if (initialMessages.isNotEmpty()) {
            rvChatMessages.scrollToPosition(initialMessages.size - 1)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ChatRepository.lastUpdateFlow.collect {
                val msgs = ChatRepository.messages
                chatAdapter.submitList(msgs) {
                    if (msgs.isNotEmpty()) {
                        rvChatMessages.scrollToPosition(msgs.size - 1)
                    }
                }
            }
        }

        btnSendChat.setOnClickListener {
            val text = etChatMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                ChatRepository.addMessage(text)
                etChatMessage.text.clear()
            }
        }

        btnDeleteAllMessages.setOnClickListener {
            showDeleteAllConfirmation()
        }
    }

    private fun showDeleteAllConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_all_messages_title)
            .setMessage(R.string.delete_all_messages_confirmation)
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                ChatRepository.deleteAllMessages()
                dialog.dismiss()
            }
            .show()
    }
}

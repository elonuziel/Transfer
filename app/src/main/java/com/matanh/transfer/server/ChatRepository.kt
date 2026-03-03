package com.matanh.transfer.server

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ChatRepository {
    private val _messages = mutableListOf<ChatMessage>()
    val messages: List<ChatMessage> get() = synchronized(this) { _messages.toList() }

    private val _lastUpdateFlow = MutableStateFlow(0L)
    val lastUpdateFlow = _lastUpdateFlow.asStateFlow()

    fun addMessage(text: String) {
        val msg = ChatMessage(text = text, timestamp = System.currentTimeMillis())
        synchronized(this) {
            _messages.add(msg)
            if (_messages.size > 100) {
                _messages.removeAt(0)
            }
        }
        _lastUpdateFlow.value = System.currentTimeMillis()
    }

    fun deleteMessageById(id: String) {
        synchronized(this) {
            val idx = _messages.indexOfFirst { it.id == id }
            if (idx >= 0) {
                _messages.removeAt(idx)
            }
        }
        _lastUpdateFlow.value = System.currentTimeMillis()
    }

    fun deleteAllMessages() {
        synchronized(this) {
            _messages.clear()
        }
        _lastUpdateFlow.value = System.currentTimeMillis()
    }
}

package com.structiq.app.feature.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.model.AIMode
import com.structiq.app.data.repository.AIAssistantRepository
import com.structiq.app.data.repository.AIMessageItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIAssistantViewModel(private val aiRepository: AIAssistantRepository = AIAssistantRepository()) : ViewModel() {

    private val _selectedMode = MutableStateFlow(AIMode.TENDER_ASSISTANT)
    val selectedMode: StateFlow<AIMode> = _selectedMode.asStateFlow()

    private val _messages = MutableStateFlow<List<AIMessageItem>>(
        listOf(
            AIMessageItem(
                id = "init_1",
                sender = "AI",
                messageText = "Welcome to **Struct-IQ AI Assistant**. Select a mode above to analyze tender documents, generate method statements, summarize BOQs, or clarify technical specifications."
            )
        )
    )
    val messages: StateFlow<List<AIMessageItem>> = _messages.asStateFlow()

    fun selectMode(mode: AIMode) {
        _selectedMode.value = mode
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = AIMessageItem(
            id = System.currentTimeMillis().toString(),
            sender = "USER",
            messageText = userText
        )
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            aiRepository.sendQuery(_selectedMode.value, userText).collect { aiMsg ->
                _messages.value = _messages.value + aiMsg
            }
        }
    }
}

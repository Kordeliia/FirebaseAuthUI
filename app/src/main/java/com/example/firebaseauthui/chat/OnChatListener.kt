package com.example.firebaseauthui.chat

import com.example.firebaseauthui.entities.Message

interface OnChatListener {
    fun deleteMessage(message: Message)
}
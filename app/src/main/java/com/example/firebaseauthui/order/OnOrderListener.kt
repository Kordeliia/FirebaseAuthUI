package com.example.firebaseauthui.order

import com.example.firebaseauthui.entities.Order

interface OnOrderListener {
    fun onStatusChange(order: Order)
    fun onStartChat(order: Order)
}
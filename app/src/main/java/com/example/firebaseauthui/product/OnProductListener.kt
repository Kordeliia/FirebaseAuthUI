package com.example.firebaseauthui.product

import com.example.firebaseauthui.entities.Product

interface OnProductListener {
    fun onClick(product: Product)
    fun onLongClick(product: Product)
}
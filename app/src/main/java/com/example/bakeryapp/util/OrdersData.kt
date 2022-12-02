package com.example.bakeryapp.util

data class OrdersData(
    var orderId: String = "",
    var userId: String = "",
    var items: MutableList<OrderItem>
)

data class OrderItem(
    var itemId: String = "",
    var amount: Int = 0,
    var combinedCost: Int = 0
)

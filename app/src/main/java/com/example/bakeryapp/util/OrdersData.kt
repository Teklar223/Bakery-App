package com.example.bakeryapp.util

import java.util.Date


data class OrdersDataPopulated(
    var orderId: String = "", //auto generated by firebase!
    var userId: String = "",
    var list: MutableList<OrderItemPopulated>,
    var totalPrice: Int = 0,
    var date: String = ""
) {
    constructor() : this("", "", mutableListOf())
}

data class OrderItemPopulated(
    var item: ItemData? = null,
    var amount: Int = 0,
)

data class OrdersData(
    var orderId: String = "", //auto generated by firebase!
    var userId: String = "",
    var list: MutableList<OrderItem>,
    var totalPrice: Int = 0,
    var date: String = ""
) {
    constructor() : this("", "", mutableListOf())
    constructor(userId: String, list: MutableList<OrderItem>) : this("", userId, list)
}

data class OrderItem(
    var itemId: String = "",
    var amount: Int = 0,
)

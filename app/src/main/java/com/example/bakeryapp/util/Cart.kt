package com.example.bakeryapp.util

import com.google.firebase.firestore.FirebaseFirestore

class CartItem(var amount: Int, val item: ItemData) {
    constructor() : this(0, ItemData()) // firebase constructor
}

class Cart(
    var items: MutableList<CartItem>,
    var userId: String = "",
) {
    val totalPrice: Int
        get() {
            var price = 0
            items.forEach { price += it.item.cost * it.amount }
            return price
        }
    val cartSize: Int
        get() {
            var size = 0
            items.forEach { size += it.amount }
            return size
        }

    /** adds an item to the cart, if item exists -> only increase amount property */
    fun addItemIncrease(itemData: ItemData, by: Int = defaultIncreaseBy): Cart {
        for (i in 0 until items.size) {
            val check = items[i].item
            if (
                check.itemId == itemData.itemId
                && check.name == itemData.name
            ) {
                items[i].amount += by
                val newCart = Cart(items = items, userId = userId)
                newCart.save()
                return newCart
            }
        }
        items.add(CartItem(item = itemData, amount = by))
        val newCart = Cart(items = items, userId = userId)
        newCart.save()
        return newCart
    }
    fun addCartItemIncrease(cartItem: CartItem, by: Int = defaultIncreaseBy): Cart {
        for (i in 0 until items.size) {
            val check = items[i].item
            if (
                check.itemId == cartItem.item.itemId
                && check.name == cartItem.item.name
            ) {
                items[i].amount = items[i].amount + by
                if (items[i].amount <= 0)
                    items.add(items[i])
                val newCart = Cart(items = items, userId = userId)
                newCart.save()
                return newCart
            }
        }

        val newCart = Cart(items = items, userId = userId)
        newCart.save()
        return newCart
    }
    /** adds an item to the cart if its empty, otherwise -> only decreases amount property */
    fun removeCartItemDecrease(cartItem: CartItem, by: Int = defaultIncreaseBy): Cart {
        for (i in 0 until items.size) {
            val check = items[i].item
            if (
                check.itemId == cartItem.item.itemId
                && check.name == cartItem.item.name
            ) {
                items[i].amount = items[i].amount - by
                if (items[i].amount <= 0)
                    items.remove(items[i])
                val newCart = Cart(items = items, userId = userId)
                newCart.save()
                return newCart
            }
        }

        val newCart = Cart(items = items, userId = userId)
        newCart.save()
        return newCart
    }

     fun save() { // saves the cart to database by user id
        FirebaseFirestore.getInstance()
            .collection(cartsCol)
            .document(userId)
            .set(this)
    }

    constructor() : this(mutableListOf()) // firebase constructor (toObject)
}
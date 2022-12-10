package com.example.bakeryapp.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class OrdersRepository {

    companion object {

        suspend fun getOrders(): List<OrdersDataPopulated> {
            return AuthInfo.user?.let { user ->
                Firebase.firestore
                    .collection(ordersCol)
                    .document(user.uid)
                    .collection(ordersCol)
                    .get()
                    .tryAwaitList(OrdersData::class.java)
                    .map {  /* per order */
                        val items =
                            it.list.map { orderItem -> /* insert item contents  */
                                OrderItemPopulated(
                                    item = itemById(ordersCol,
                                        orderItem.itemId,
                                        ItemData::class.java),
                                    amount = orderItem.amount
                                )
                            }
                        /* return populated order */
                        return@map OrdersDataPopulated(it.orderId, it.userId, items.toMutableList())
                    }
            } ?: run { listOf() } /* user not logged in */

        }

        suspend fun insertOrder(orderData: OrdersData) {
            AuthInfo.user?.let { user ->
                val newDoc = Firebase.firestore
                    .collection(ordersCol)
                    .document(user.uid)
                    .collection(ordersCol)
                    .add(orderData)
                    .await()
                val id = newDoc.id
                newDoc.update("orderId", id)
            }
        }

    }

}
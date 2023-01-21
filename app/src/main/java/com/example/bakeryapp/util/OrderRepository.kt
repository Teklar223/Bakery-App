package com.example.bakeryapp.util

import com.example.bakeryapp.screen.dateAsString
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class LimitCap(val limit: Int = 0)
class OrderRepository {
    companion object {

        /* updates the limit for an item */
        suspend fun setLimitCap(
            date: String,
            itemData: ItemData,
            increaseBy: Int = defaultIncreaseBy,
        ) {

            val newLimit = LimitCap(limit = /*itemData.limit +*/ increaseBy)
            FirebaseFirestore
                .getInstance()
                .collection(limitCol)
                .document(date)
                .collection(itemsCol)
                .document(itemData.itemId)
                .set(newLimit)
                .await()
        }

        /* checks if an item is at limit */
        suspend fun isAtLimitCap(date: String, itemData: ItemData): Boolean {
            try {
                val limit = FirebaseFirestore
                    .getInstance()
                    .collection(limitCol)
                    .document(date)
                    .collection(itemsCol)
                    .document(itemData.itemId)
                    .get()
                    .tryAwait(LimitCap::class.java) ?: return false
                return limit.limit >= itemData.limit
            } catch (e: java.lang.Exception) {
                return false
            }
        }


        /* Populates an order with item data */
        suspend fun toPopulated(list: MutableList<OrdersData>): MutableList<OrdersDataPopulated> {
            return list.map {  /* per order */
                val items =
                    it.list.map { orderItem -> /* insert item contents  */
                        OrderItemPopulated(
                            item = itemById(
                                itemsCol,
                                orderItem.itemId,
                                ItemData::class.java
                            ),
                            amount = orderItem.amount
                        )
                    }
                /* return populated order */
                val populated = OrdersDataPopulated(it.orderId,
                    it.userId,
                    it.userName,
                    items.toMutableList())
                populated.date =
                    dateAsString(LocalDateTime.ofInstant(Instant.ofEpochMilli(it.date),
                        ZoneId.systemDefault()), full = true)
                return@map populated
            }.toMutableList()
        }
    }

    /* gets all the orders in 24 hours before and 24 hours after today */
    suspend fun getOrdersForDay(): MutableList<OrdersDataPopulated> {
        val today = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val docResponse = FirebaseFirestore.getInstance()
            .collection(ordersCol)
            .get()
            .await()

        val returnList = mutableListOf<OrdersData>()
        docResponse.documents.forEach { userOrdersDocument ->
            val orders = userOrdersDocument.reference
                .collection(ordersCol)
                .get()
                .tryAwaitList(OrdersData::class.java)
            returnList.addAll(orders.filter { orderData ->
                kotlin.math.abs(today - orderData.date) < 24 * 60 * 60 * 1000
            })
        }
        return toPopulated(returnList)
    }

}
package com.example.bakeryapp.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
data class LimitCap(val limit: Int = 0)
class OrderRepository {
    companion object {

        suspend fun setLimitCap(date: String, itemData: ItemData) {

           val earlier =  FirebaseFirestore
                .getInstance()
                .collection(limitCol)
                .document(date)
                .collection(itemsCol)
                .document(itemData.itemId)
                .get()
                .tryAwait(LimitCap::class.java) ?: LimitCap(limit = 0)

            FirebaseFirestore
                .getInstance()
                .collection(limitCol)
                .document(date)
                .collection(itemsCol)
                .document(itemData.itemId)
                .set(LimitCap(limit = earlier.limit + 1))
                .await()
        }

        suspend fun isAtLimitCap(date: String, itemData: ItemData): Boolean {
            val limit = FirebaseFirestore
                .getInstance()
                .collection(limitCol)
                .document(date)
                .collection(itemsCol)
                .document(itemData.itemId)
                .get()
                .tryAwait(LimitCap::class.java) ?: return false
            return limit.limit >= itemData.limit
        }


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
                return@map OrdersDataPopulated(it.orderId,
                    it.userId,
                    it.userName,
                    items.toMutableList())
            }.toMutableList()
        }
    }

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
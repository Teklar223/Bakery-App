package com.example.bakeryapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDateTime
import com.example.bakeryapp.util.*


/**
 * This class is the "backend" of our application, responsible for all Firebase Auth and Cloudstore
 * DB actions.
 * Except for 'Cart'
 */
class SharedViewModel : ViewModel() {
    private lateinit var navController: NavController
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    fun setNav(navController: NavController) {
        this.navController = navController
    }

    /* ************************************************************************************** */
    /* ************************************* Auth ******************************************* */
    /* ************************************************************************************** */

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        posCallback: () -> Unit,
        error: (e: java.lang.Exception) -> Unit,
    ) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)

            AuthInfo.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        posCallback()
                    } else if (task.exception != null) {
                        error(task.exception!!)
                    }
                }

            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signWithCredential(credential: AuthCredential) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            AuthInfo.auth.signInWithCredential(credential).await()
            loadingState.emit(LoadingState.LOADED)
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signOut() {
        AuthInfo.auth.signOut()
        AuthInfo.user = null
    }

    /* ************************************************************************************** */
    /* ************************************ Orders ****************************************** */
    /* ************************************************************************************** */

    suspend fun getOrders(): List<OrdersDataPopulated> {
        return AuthInfo.user?.let { user ->
            Firebase.firestore
                .collection(ordersCol)
                .document(user.uid)
                .collection(ordersCol)
                .limit(ordersToShow.toLong())
                .get()
                .tryAwaitList(OrdersData::class.java)
                .map {  /* per order */
                    val items =
                        it.list.map { orderItem -> /* insert item contents  */
                            OrderItemPopulated(
                                item = itemById(
                                    ordersCol,
                                    orderItem.itemId,
                                    ItemData::class.java
                                ),
                                amount = orderItem.amount
                            )
                        }
                    /* return populated order */
                    return@map OrdersDataPopulated(it.orderId, it.userId, items.toMutableList())
                }
        } ?: run { listOf() } /* user not logged in */

    }

    private suspend fun insertOrder(orderData: OrdersData) {
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

    @RequiresApi(Build.VERSION_CODES.O) //LocalDateTime.now() triggers if phone has access to api 26
    fun checkout(cart: Cart) = viewModelScope.launch {
        AuthInfo.user?.let { user ->
            val itemList: MutableList<OrderItem> = mutableListOf()
            var totalCost = 0
            for (itemWrapper in cart.items) {
                totalCost += itemWrapper.item.cost * itemWrapper.amount
                itemList.add(
                    OrderItem(
                        itemId = itemWrapper.item.itemId,
                        itemWrapper.amount
                    )
                )
            }

            val order = OrdersData(
                userId = user.uid,
                list = itemList,
                totalPrice = totalCost,
                date = LocalDateTime.now().toString()
            )
            insertOrder(order)
        }
    }

    // function to populate orders
    fun populateOrders() = viewModelScope.launch {
        AuthInfo.user?.let { user ->
            val items = getItems()
            for (i in 0 until 10) {
                val list: MutableList<OrderItem> = mutableListOf()
                for (j in 0 until 3) {
                    list.add(
                        OrderItem(
                            itemId = items.random().itemId,
                            ((Math.random() * 3) + 1).toInt()
                        )
                    )
                }
                val order = OrdersData(
                    user.uid,
                    list
                )
                insertOrder(order)
            }
        }
    }

    /* ************************************************************************************** */
    /* ***************************** Items and Materials ************************************ */
    /* ************************************************************************************** */

    suspend fun getItems() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection(itemsCol)
            .get()
            .tryAwaitList(ItemData::class.java)
    }.await()

    suspend fun getMaterials() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection(materialsCol)
            .get()
            .tryAwaitList(MaterialsData::class.java)
    }.await()
}

/* ************************************************************************************** */
/* ************************************* Helpers **************************************** */
/* ************************************************************************************** */

suspend fun <T> Task<QuerySnapshot>.tryAwaitList(classData: Class<T>): MutableList<T> {
    return try {
        withContext(Dispatchers.IO) {
            val output = await().toObjects(classData)
            return@withContext withContext(Dispatchers.Main) { output }
        }
    } catch (e: Exception) {
        println("There was an error ${e.message}")
        mutableListOf()
    }
}

suspend fun <T> Task<DocumentSnapshot>.tryAwait(classData: Class<T>): T? {
    return try {
        // resource fetch at io
        withContext(Dispatchers.IO) {
            val output = await().toObject(classData)
            // screen updates at main
            return@withContext withContext(Dispatchers.Main) { output }
        }
    } catch (e: Exception) {
        println("There was an error ${e.message}")
        return null
    }
}

suspend fun <T> itemById(
    collectionName: String,
    id: String,
    fromClass: Class<T>,
): T? {
    return Firebase.firestore
        .collection(collectionName)
        .document(id)
        .get()
        .tryAwait(fromClass)
}
package com.example.bakeryapp.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakeryapp.util.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import com.google.firebase.auth.*


/**
 * This class is the "backend" of our application, responsible for all Firebase Auth and Cloudstore
 * DB actions.
 * Except for 'Cart'
 */
class SharedViewModel : ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    fun addItem(itemData: ItemData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AdminRepository.addItem(itemData)
            }
        }
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

    fun registerWithEmailAndPassword(
        email: String,
        password: String,
        posCallback: () -> Unit,
        error: (e: java.lang.Exception) -> Unit,
    ) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)

            AuthInfo.auth.createUserWithEmailAndPassword(email, password)
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

    fun signWithCredential(
        credential: AuthCredential,
        posCallback: () -> Unit,
        error: (e: java.lang.Exception) -> Unit,
    ) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            AuthInfo.auth.signInWithCredential(credential)
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

    fun signOut() {
        AuthInfo.auth.signOut()
        AuthInfo.user = null
        AuthInfo.isAdmin.value = false
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
                                    itemsCol,
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

    suspend fun checkAdmin(email: String) = CoroutineScope(Dispatchers.IO).async {

        var admins = Firebase.firestore
            .collection(adminsCol)
            .get()
            .tryAwaitList(AdminData::class.java)

        for (item in admins) {
            if (item.ID == email) {
                return@async true
            }
        }
    }.await()
}

/* ************************************************************************************** */
/* ************************************* Helpers **************************************** */
/* ************************************************************************************** */

/** General purpose wrapper for handling Task<QuerySnapshot> results from Firebase,
 *  and converting them to our data classes */
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

/** General purpose wrapper for handling Task<DocumentSnapshot> results from Firebase,
 *  and converting them to our data classes */
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
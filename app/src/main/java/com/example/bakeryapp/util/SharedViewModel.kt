package com.example.bakeryapp.util

import android.os.Build
import android.os.StrictMode
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakeryapp.util.*
import com.example.bakeryapp.util.AuthInfo.isAdmin
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
import com.google.firebase.firestore.auth.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalField


/**
 * This class is the "backend" of our application, responsible for all Firebase Auth and Cloudstore
 * DB actions.
 * Except for 'Cart'
 */
class SharedViewModel : ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    init {
        viewModelScope.launch {
            isAdmin.value = checkAdmin(AuthInfo.user?.uid)
            Log.d("ISADMIN", isAdmin.toString())
        }
    }

    suspend fun getTodayOrders(): List<OrdersDataPopulated> {
        return withContext(Dispatchers.IO) {
            OrderRepository().getOrdersForDay()
        }
    }

    fun addItem(itemData: ItemData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AdminRepository.addItem(itemData)
            }
        }
    }

    fun addMaterial(material: MaterialsData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AdminRepository.addMaterial(material)
            }
        }
    }

/* ************************************************************************************** */
/* ************************************* Auth ******************************************* */
/* ************************************************************************************** */

    //-------------------------------------login-------------------------------------------//
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

    //-----------------------------------------------------------------------------------------//

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
            OrderRepository.toPopulated(Firebase.firestore
                .collection(ordersCol)
                .document(user.uid)
                .collection(ordersCol)
                .limit(ordersToShow.toLong())
                .get()
                .tryAwaitList(OrdersData::class.java))

        } ?: run { listOf() } /* user not logged in */

    }

    private suspend fun clearCart(cart: Cart) {
        // clear session cart
        CartRepository.clearSessionCart()
        // clear cart from database
        Firebase.firestore
            .collection(cartsCol)
            .document(cart.userId)
            .set(CartRepository.cart.value!!)
            .await()
    }

    private suspend fun insertOrder(orderData: OrdersData) {
        AuthInfo.user?.let { user ->
            val doc = Firebase.firestore
                .collection(ordersCol)
                .document(user.uid)
            val exists = doc
                .get()
                .await()
                .exists()
            if (exists) {
                val newOrder = doc.collection(ordersCol)
                    .add(orderData)
                    .await()

                val id = newOrder.id
                newOrder.update("orderId", id)
            } else {
                doc.set({})
                val newOrder = doc.collection(ordersCol)
                    .add(orderData)
                    .await()
                val id = newOrder.id
                newOrder.update("orderId", id)
            }
        }
    }

    fun checkout(cart: Cart, localDate: LocalDateTime) = viewModelScope.launch {
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
                userName = user.email!!.split("@")[0],
                totalPrice = totalCost,
                date = localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            )
            // save order
            insertOrder(order)
            // clear cart
            clearCart(cart)
        }
    }

/* ************************************************************************************** */
/* ***************************** Items and Materials ************************************ */
/* ************************************************************************************** */

//------------------------------------------Items------------------------------------------//

    suspend fun getItems(): List<ItemData> = coroutineScope {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        val url = URL("http://192.168.56.1:3000/items")

        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.connectTimeout = 5000
        con.readTimeout = 5000
        con.connect()

        val jsonString = con.inputStream.use { it.reader().readText() }
        convertToItemDataList(JSONArray(jsonString))
    }

    fun convertToItemDataList(jsonArray: JSONArray): List<ItemData> {
        val itemDataList = mutableListOf<ItemData>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val itemData = ItemData(
                jsonObject.getString("itemId"),
                jsonObject.getString("name"),
                jsonObject.getString("image"),
                jsonObject.getString("description"),
                jsonObject.getInt("limit"),
                jsonObject.getInt("cost"),
                jsonObject.getString("currency")
            )
            itemDataList.add(itemData)
        }
        return itemDataList
    }
//------------------------------------------Materials------------------------------------------//

    suspend fun getMaterials(): List<MaterialsData> = coroutineScope {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        val url = URL("http://192.168.56.1:3000/materials")

        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.connectTimeout = 5000
        con.readTimeout = 5000
        con.connect()

        val jsonString = con.inputStream.use { it.reader().readText() }
        convertToMaterialDataList(JSONArray(jsonString))
    }

    fun convertToMaterialDataList(jsonArray: JSONArray): List<MaterialsData> {
        val materialDataList = mutableListOf<MaterialsData>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val materialData = MaterialsData(
                jsonObject.getString("materialId"),
                jsonObject.getString("name"),
                jsonObject.getString("description"),
                jsonObject.getInt("cost"),
                jsonObject.getString("currency"),
                jsonObject.getString("unit"),
                jsonObject.getString("contactInfoName"),
                jsonObject.getString("contactInfoPhone"),



            )
            materialDataList.add(materialData)
        }
        return materialDataList
    }

//-----------------------------------------Admin Check---------------------------------------------/
    // modified here to direct fetch by id

    suspend fun checkAdmin(userId: String?) : Boolean = coroutineScope {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        val url = URL("http://192.168.56.1:3000/adminCheck?userID="+userId)

        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.connectTimeout = 5000
        con.readTimeout = 5000
        con.connect()

        val jsonString = con.inputStream.use { it.reader().readText() }
        return@coroutineScope jsonString=="true"
    }
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
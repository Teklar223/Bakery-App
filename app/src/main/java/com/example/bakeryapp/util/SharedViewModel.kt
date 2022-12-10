package com.example.bakeryapp.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.example.bakeryapp.MainActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.google.firestore.v1.StructuredQuery.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


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


/**
 * This class is the "backend" of our application, responsible for all Firebase Auth and Cloudstore
 * DB actions.
 */
class SharedViewModel : ViewModel() {
    // private lateinit var mainActivity: MainActivity /*  VERY DANGEROUS - memory leaks */
    private lateinit var navController: NavController
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    fun setNav(navController: NavController) {
        this.navController = navController
    }


    // function to populate orders
    fun populateOrders() = viewModelScope.launch {
        AuthInfo.user?.let { user ->
            val items = getItems()
            for (i in 0 until 10) {
                val list: MutableList<OrderItem> = mutableListOf()
                for (j in 0 until 3) {
                    list.add(OrderItem(itemId = items.random().itemId,
                        ((Math.random() * 3) + 1).toInt()))
                }
                val order = OrdersData(
                    user.uid,
                    list
                )
                OrdersRepository.insertOrder(order)
            }
        }
    }


    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        reloadActivity: () -> Unit,
        error: (e: java.lang.Exception) -> Unit,
    ) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)

            AuthInfo.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        reloadActivity() // call positive callback
                    } else if (task.exception != null) {
                        error(task.exception!!) // call negative callback with exception
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

    /**examples for an obsolete user data class:
    TODO: remove after everything is implemented
    fun saveData(
    context: Context,
    navController: NavController
    ) = CoroutineScope(Dispatchers.IO).launch {
    try {
    database.collection(profilesCol)
    .add(user)
    .addOnSuccessListener {
    Toast.makeText(context, "successfully saved data", Toast.LENGTH_SHORT).show()
    navController.popBackStack()
    }
    }
    catch (e: Exception){
    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
    }

    fun retrieveData(
    userID: String,
    context: Context,
    navController: NavController,
    //data: (UserData) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
    try {
    /*
    database.collection(profilesCol)
    .whereEqualTo("userID",userID)
    .get()
    .addOnSuccessListener {
    Toast.makeText(context, "Logging you in!", Toast.LENGTH_SHORT).show()
    navController.popBackStack()
    }
    .addOnFailureListener {
    Toast.makeText(
    context,
    "Could not retrieve your information, make sure it's accurate!",
    Toast.LENGTH_SHORT).show()
    }
    */
    }
    catch (e: Exception){
    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
    }*/
}
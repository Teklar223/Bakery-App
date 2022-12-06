package com.example.bakeryapp.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


suspend fun <T> Task<QuerySnapshot>.tryAwaitList(classData:Class<T>) : MutableList<T> {
    return try {
        withContext(Dispatchers.IO) {
            val output = await().toObjects(classData)
            return@withContext withContext(Dispatchers.Main) {
                 output
            }
        }
    }catch(e:Exception) {
        println("There was an error ${e.message}")
        mutableListOf()
    }
}

/**
 * This class is the "backend" of our application, responsible for all Cloudstore DB actions.
 */
class SharedViewModel: ViewModel() {

    fun getOrders() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection("orders")//TODO: change to util.constants.ordersCol
            .get()
            .tryAwaitList(OrdersData::class.java)
    }

    /**examples for an obsolete user data class:

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
package com.example.bakeryapp.util

import android.util.Log
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
import com.example.bakeryapp.util.itemsCol
import com.example.bakeryapp.util.materialsCol
import com.example.bakeryapp.util.ordersCol
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Objects


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
 * This class is the "backend" of our application, responsible for all Firebase Auth and Cloudstore
 * DB actions.
 */
class SharedViewModel: ViewModel() {
    private lateinit var mainActivity: MainActivity
    private lateinit var navController: NavController
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    fun setNav(navController: NavController){
        this.navController = navController
    }

    fun setActivity(mainActivity: MainActivity){
        this.mainActivity = mainActivity
    }

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            AuthInfo.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        mainActivity.reloadActivity()
                    } else {

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

    fun signOut(){
        AuthInfo.auth.signOut()
        AuthInfo.user = null
    }

    suspend fun getOrders() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection(ordersCol)
            .get()
            .tryAwaitList(OrdersData::class.java)
    }.await()

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


    suspend fun addMaterials() = CoroutineScope(Dispatchers.IO).async {

        Log.d("addMaterials", "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
        //val amit = MaterialsData("asa","asas","sda",1111,"sasa")
        //Firebase.firestore.collection(materialsCol).document("lalala")
    }.await()

}
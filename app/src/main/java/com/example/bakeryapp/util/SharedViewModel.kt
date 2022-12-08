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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


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

    fun getOrders() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection("orders")//TODO: change to util.constants.ordersCol
            .get()
            .tryAwaitList(OrdersData::class.java)
    }

    fun getItems() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection("items")//TODO: change to util.constants.itemsCol
            .get()
            .tryAwaitList(ItemData::class.java)
    }

    fun getMaterials() = CoroutineScope(Dispatchers.IO).async {
        return@async Firebase.firestore
            .collection("materials")//TODO: change to util.constants.itemsCol
            .get()
            .tryAwaitList(MaterialsData::class.java)
    }

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
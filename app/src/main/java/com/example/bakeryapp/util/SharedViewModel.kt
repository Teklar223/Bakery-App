package com.example.bakeryapp.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.bakeryapp.MainActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * This class is the "backend" of our application, responsible for all Firebase Auth and Cloudstore
 * DB actions.
 */
class SharedViewModel: ViewModel() {
    val loadingState = MutableStateFlow(LoadingState.IDLE)

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            AuthInfo.auth.signInWithEmailAndPassword(email, password).await()
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
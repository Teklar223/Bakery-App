package com.example.bakeryapp.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This class is the "backend" of our application, responsible for all Cloudstore DB actions.
 */
class SharedViewModel: ViewModel() {

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
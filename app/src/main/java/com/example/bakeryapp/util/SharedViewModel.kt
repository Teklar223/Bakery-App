package com.example.bakeryapp.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel: ViewModel() {

    fun saveData(
        user: UserData,
        context: Context,
        navController: NavController
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            database.collection(profilesCol)
                .add(user)
                // TODO: can we use our own id instead of the auto generated? AND do we need to?
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
        data: (UserData) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
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
        }
        catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteData(
        userID: String,
        context: Context,
        navController: NavController
    ) = CoroutineScope(Dispatchers.IO).launch {
        /* TODO!
        val firestoreRef = Firebase.firestore
            .collection("user")
            .document(userID)

        try {
            firestoreRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
        }
        catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }

         */
    }
}
package com.example.bakeryapp.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel: ViewModel() {

    fun saveData(
        user: UserData,
        context: Context,
        database: FirebaseFirestore //TODO: try to pass in constructor instead !
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            database.collection("profiles")
                .add(user)// TODO: can we use our own id instead of the auto generated? AND do we need to?
                .addOnSuccessListener {
                    Toast.makeText(context, "successfully saved data", Toast.LENGTH_SHORT).show()
                }
        }
        catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun retrieveData(
        userID: String,
        context: Context,
        data: (UserData) -> Unit,
    ) = CoroutineScope(Dispatchers.IO).launch {
        /* TODO!
        val firestoreRef = Firebase.firestore
            .collection("user")
            .document(userID)

        try {
            firestoreRef.get()
                .addOnSuccessListener {
                    if (it.exists()){
                        val userData = it.toObject<UserData>()!!
                        data(userData) // if null, returns 'Unit' class (singleton).
                    }
                    else{
                        Toast.makeText(context, "No user data found!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        catch (e: Exception){
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
         */
    }

    fun deleteData(
        userID: String,
        context: Context,
        navController: NavController,
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
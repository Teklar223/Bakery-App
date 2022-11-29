package com.example.bakeryapp.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel: ViewModel() {

    fun saveData(
        userData: UserData,
        context: Context,
        database: FirebaseDatabase
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val dbRef = database.getReference("Profiles")
            dbRef.setValue(userData.userID,userData.firstName) // TODO: userData.tojson() method!
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
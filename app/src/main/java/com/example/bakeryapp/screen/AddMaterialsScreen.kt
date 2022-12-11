package com.example.bakeryapp.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.util.MaterialsData
import com.example.bakeryapp.util.SharedViewModel
import com.example.bakeryapp.util.materialsCol
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

@Composable
fun AddMaterialScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
){

    /** main layout **/
    val go = Firebase.firestore.collection(materialsCol).document("lalala")
    val amit = MaterialsData("test2","asas","sda",2,"sasa")
    LaunchedEffect(Dispatchers.IO) {
        go.set(amit).await()
        val mat = go.get().await().toObject(MaterialsData::class.java)
        //val materialsData = sharedViewModel.addMaterials().set(amit).await()


        Log.d("addMaterials", "amit was here")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        /** Back button **/
        Row(
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ){
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
            }
        }

        Text(text = "amit was here!")
    }


}


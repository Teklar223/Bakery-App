package com.example.bakeryapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.util.SharedViewModel
import com.example.bakeryapp.util.UserData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddDataScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    database: FirebaseFirestore
){
    var userID: String by remember { mutableStateOf("") }
    var firstName: String by remember { mutableStateOf("") }
    var lastName: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }
    var age: String by remember { mutableStateOf("") }
    var ageInt: Int by remember { mutableStateOf(0) } //TODO: do we need this separation?

    val context = LocalContext.current

    /** main layout **/

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

        /** GET data layout **/
        Column(
            modifier = Modifier
                .padding(start = 60.dp, end = 60.dp, bottom = 50.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /** User ID **/
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userID,
                onValueChange = {
                    userID = it
                },
                label = {
                    Text(text = "User ID")
                }
            )
            /** First Name **/
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = firstName,
                onValueChange = {
                    firstName = it
                },
                label = {
                    Text(text = "First Name")
                }
            )
            /** Last Name **/
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = lastName,
                onValueChange = {
                    lastName = it
                },
                label = {
                    Text(text = "Last Name")
                }
            )
            /** Email **/
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Email")
                }
            )
            /** Age **/
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = age,
                onValueChange = {
                    age = it                         //TODO: age + ageInt separation plays here
                    if (age.isNotEmpty()) {
                        ageInt = age.toInt()
                    }
                },
                label = {
                    Text(text = "Age")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            /** Save Button **/
            Button(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth(),
                onClick = {
                    val userData = UserData(
                        userID = userID,
                        firstName = firstName,
                        lastName = lastName,
                        age = age.toInt(), //!! -1, TODO: different handling of null, and reason why we don't need ageInt?
                        //https://kotlinlang.org/docs/null-safety.html#the-operator
                        email = email
                    )

                    sharedViewModel.saveData(user = userData, context = context, database = database)
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
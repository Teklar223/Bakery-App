package com.example.bakeryapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.util.ItemData
import com.example.bakeryapp.util.OrdersData
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TEMPItemsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
){
    /**
     * this screen is only for testing the ability to pull and layout items from the DB
     * and onto the screen during early dev' stages!
     * TODO: move all of these functionalities to MainScreen!
     */

    val context = LocalContext.current
    val items: MutableState<List<ItemData>> = remember {
        mutableStateOf(listOf())
    }

    LaunchedEffect(key1 = items) {
        val itemsData = sharedViewModel.getItems().await()
        items.value = itemsData
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

        /* @TODO Fetch items for each order list + Styling */
        for(item in items.value) {
            Text(text = "Item ID: ${item.itemId}")
            Row {
                Column {
                    Text(text = "Item Cost:${item.cost}")
                    Text(text = "Item Currency: ${item.currency}")
                }
            }
        }
    }
}
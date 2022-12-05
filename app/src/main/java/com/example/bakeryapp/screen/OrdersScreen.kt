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
import com.example.bakeryapp.util.OrdersData
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firestore.v1.StructuredQuery.Order
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@Composable
fun OrdersScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val orders: MutableState<List<OrdersData>> = remember {
        mutableStateOf(listOf())
    }

    LaunchedEffect(key1 = orders) {
       val ordersData = sharedViewModel.getOrders().await()
        orders.value = ordersData
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
        for(order in orders.value) {
            Text(text = "The order is from user: ${order.userId}")
            Row {
            for(item in order.list) {
                Column {
                    Text(text = "Item id:${item.itemId}")
                    Text(text = "Item Amount: ${item.amount}")
                }
            }
            }
        }

    }
}
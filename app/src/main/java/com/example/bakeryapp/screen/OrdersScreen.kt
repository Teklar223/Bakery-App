package com.example.bakeryapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.example.bakeryapp.util.OrdersData
import com.example.bakeryapp.util.SharedViewModel


@Composable
fun OrdersScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    val orders: MutableState<List<OrdersData>> = remember {
        mutableStateOf(listOf())
    }

    /* runs only if 'orders' change'*/
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
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
            }
        }

        /* @TODO Fetch items for each order list + Styling */
        for (order in orders.value) {
            Column(modifier = Modifier
                .background(Color.LightGray)
                .padding(16.dp)) {
                Text(text = "The order is from user: ${order.userId}")
                Row {
                    for (item in order.list) {
                        Column {
                            Text(text = "Item id:${item.itemId}")
                            Text(text = "Item Amount: ${item.amount}")
                        }
                    }
                }
            }
        }
    }

}

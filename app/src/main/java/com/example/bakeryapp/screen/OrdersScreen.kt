package com.example.bakeryapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.example.bakeryapp.util.*


@Composable
fun OrdersScreen( 
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    val orders: MutableState<List<OrdersDataPopulated>> = remember {
        mutableStateOf(listOf())
    }
    /* runs only if 'orders' change'*/
    LaunchedEffect(key1 = orders) {
        val ordersData = sharedViewModel.getOrders()
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
            Text(text = "Order id: ${order.orderId}", modifier = Modifier.padding(8.dp))
            Column(modifier = Modifier
                .padding(16.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.Transparent)
                .padding(8.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 2.dp)) {
                    for (orderItemData in order.list) {
                        Row {
                            Column(modifier = Modifier
                                .padding(0.dp, 8.dp)) {
                                orderItemData.item?.let { itemData ->
                                    Text(text = "Item Name: ${itemData.name}",
                                        style = TextStyle(fontWeight = FontWeight.Bold))
                                    Text(text = "Item Description: ${itemData.description}")
                                    Text(text = "Item Amount: ${orderItemData.amount}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

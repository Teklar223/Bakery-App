package com.example.bakeryapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.example.bakeryapp.util.*
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth


@Composable
fun OrdersScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    val orders: MutableState<List<OrdersDataPopulated>?> = remember {
        mutableStateOf(null)
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

        orders.value?.let { orders ->
            if (orders.isEmpty()) { /* no orders */
                Text(text = "No orders....", modifier = Modifier.padding(16.dp))
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                for (order in orders) {
                    Text(text = order.date, modifier = Modifier.padding(8.dp))
                    OrderItemsList(order) /* item list */
                }
            }
        } ?: Text(modifier = Modifier.padding(16.dp),
            text = "Loading orders...") /* still loading (orders object is null) */
    }
}

@Composable
fun OrderItemsList(order: OrdersDataPopulated) {
    Column(modifier = Modifier
        .padding(16.dp)
        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
        .background(Color.Transparent)
        .padding(8.dp)
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 200.dp)
            .padding(0.dp, 2.dp),
            content = {
                items(order.list) { itemData ->
                    Row {
                        Column(modifier = Modifier
                            .padding(0.dp, 8.dp)) {
                            itemData.item?.let { item ->
                                Text(text = "Item Name: ${item.name}",
                                    style = TextStyle(fontWeight = FontWeight.Bold))
                                Text(text = "Item Description: ${item.description}")
                                Text(text = "Item Amount: ${itemData.amount}")
                            }
                        }
                    }
                }
            })
    }
}

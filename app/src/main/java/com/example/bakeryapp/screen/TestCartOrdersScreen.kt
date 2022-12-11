@file:OptIn(ExperimentalFoundationApi::class)

package com.example.bakeryapp.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bakeryapp.util.*


/** A test screen for the item list & cart list **/
@Composable
fun TestCartOrdersScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var products: List<ItemData> by remember { mutableStateOf(listOf()) }
    var cart: Cart by remember { mutableStateOf(Cart()) }

    LaunchedEffect(key1 = products)
    {
        products = sharedViewModel.getItems()
        products.forEach { println(it) }
    }

    LaunchedEffect(key1 = cart) {
        cart = CartRepository.getSessionCart()
    }


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        PopulateOrdersButton(sharedViewModel)
        _Header(text = "Products")
        _ProductItemList(products)
        { product -> cart = cart.addItemIncrease(product) }

        _Header(text = "Cart items")
        _CartItemList(cart)
        { newCart -> cart = newCart }
    }
}

@Composable
fun _Header(text: String) {
    Text(
        modifier = Modifier.padding(6.dp),
        text = text,
        style = TextStyle(fontSize = 24.sp,
            fontWeight = FontWeight.Bold))
}

/**  Product List -  shows all items in the application  **/
@Composable
fun _ProductItemList(
    products: List<ItemData>,
    addItem: (itemData: ItemData) -> Unit,
) {
    LazyColumn(modifier = Modifier
        .fillMaxHeight(0.5f)
        .fillMaxWidth()
        .border(border = BorderStroke(1.dp, LightGray), RoundedCornerShape(8.dp))
        .padding(8.dp)) {
        products.forEach { item ->
            items(1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = item.name, style = TextStyle(fontSize = 24.sp))
                        Text(text = item.description, style = TextStyle(fontSize = 16.sp))
                    }
                    _AddItemButton(item = item) { addItem(it) }
                }
            }
        }
    }
}


/**  Cart List -  shows all items in the the current user's cart (LOGGED IN ONLY)  **/
@Composable
fun _CartItemList(
    cart: Cart,
    update: (cart: Cart) -> Unit,
) {
    LazyColumn(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .border(border = BorderStroke(1.dp, LightGray), RoundedCornerShape(8.dp))
        .padding(8.dp)) {
        cart.items.forEach { cartItem ->
            items(1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = cartItem.item.name, style = TextStyle(fontSize = 24.sp))
                        Text(text = cartItem.item.description, style = TextStyle(fontSize = 16.sp))
                        Text(text = "Amount: ${cartItem.amount}",
                            style = TextStyle(fontSize = 16.sp))
                    }
                    _RemoveItemButton(item = cartItem) { update(cart.removeItemDecrease(it)) }
                }
            }
        }
    }
}


/**  Populate orders button - adds random orders to the current user  **/
@Composable
fun PopulateOrdersButton(
    sharedViewModel: SharedViewModel,
) {
    val context = LocalContext.current
    var state :Int by  remember {
        mutableStateOf(0)
    }

    Button(onClick = {
        state += 1
        if (state == 2) {
            sharedViewModel.populateOrders()
            Toast.makeText(context, "Populated orders successfully", Toast.LENGTH_SHORT).show()
            state = 0
        } else {
            Toast.makeText(context,
                "Click again to populate, (WARNING: INSERTS ORDERS TO DATABASE!)",
                Toast.LENGTH_SHORT).show()
        }
    }) {
        Text(text = "Populate order list for current user")
    }
}

@Composable
fun _AddItemButton(
    item: ItemData,
    addItem: (item: ItemData) -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White),
        modifier = Modifier.fillMaxWidth(0.85f),
        onClick = {
            addItem(item)
        },
        interactionSource = MutableInteractionSource(

        )) {
        Text(text = "Add to cart",
            style = TextStyle(fontSize = 18.sp))
    }
}

@Composable
fun _RemoveItemButton(
    item: CartItem,
    removeItem: (item: CartItem) -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White),
        modifier = Modifier.fillMaxWidth(0.85f),
        onClick = {
            removeItem(item)
        }) {
        Text(text = "Remove",
            style = TextStyle(fontSize = 18.sp))
    }
}

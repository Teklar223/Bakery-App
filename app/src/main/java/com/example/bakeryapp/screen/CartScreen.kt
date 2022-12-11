package com.example.bakeryapp.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bakeryapp.util.*

@Composable
fun CartScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var cart: Cart by remember { mutableStateOf(Cart()) }

    LaunchedEffect(key1 = cart) {
        cart = CartRepository.getSessionCart()
    }

    Row(
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ){
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ){
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        cartHeader(
            sharedViewModel = sharedViewModel,
            text = "Cart items",
            cart = cart
        )
        CartItemList(cart)
        { newCart -> cart = newCart }
    }
}

@Composable
fun cartHeader(sharedViewModel: SharedViewModel, text: String, cart: Cart) {
    Text(
        modifier = Modifier.padding(6.dp),
        text = text,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(0.85f),
        onClick = {
            sharedViewModel.checkout(cart)
        },
        interactionSource = MutableInteractionSource(

        )
    ) {
        Text(
            text = "Checkout",
            style = TextStyle(fontSize = 18.sp)
        )
    }
}

/**  Cart List -  shows all items in the the current user's cart (LOGGED IN ONLY)  **/
@Composable
fun CartItemList(
    cart: Cart,
    update: (cart: Cart) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        cart.items.forEach { cartItem ->
            items(1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = cartItem.item.name, style = TextStyle(fontSize = 24.sp))
                        Text(text = cartItem.item.description, style = TextStyle(fontSize = 16.sp))
                        Text(
                            text = "Amount: ${cartItem.amount}",
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                    RemoveItemButton(item = cartItem) { update(cart.removeItemDecrease(it)) }
                }
            }
        }
    }
}

@Composable
fun RemoveItemButton(
    item: CartItem,
    removeItem: (item: CartItem) -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(0.85f),
        onClick = {
            removeItem(item)
        }) {
        Text(
            text = "Remove",
            style = TextStyle(fontSize = 18.sp)
        )
    }
}
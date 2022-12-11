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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bakeryapp.util.*

@Composable
fun TEMPItemsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    /**
     * this screen is only for testing the ability to pull and layout items from the DB
     * and onto the screen during early dev' stages!
     * TODO: move all of these functionalities to MainScreen!
     */
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

    Row(
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        itemHeader(text = "Products")
        ProductItemList(products)
        { product -> cart = cart.addItemIncrease(product) }
    }
}

@Composable
fun itemHeader(text: String) {
    Text(
        modifier = Modifier.padding(6.dp),
        text = text,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun AddItemButton(
    item: ItemData,
    addItem: (item: ItemData) -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(0.85f),
        onClick = {
            addItem(item)
        },
        interactionSource = MutableInteractionSource(

        )
    ) {
        Text(
            text = "Add to cart",
            style = TextStyle(fontSize = 18.sp)
        )
    }
}

/**  Product List -  shows all items in the application  **/
@Composable
fun ProductItemList(
    products: List<ItemData>,
    addItem: (itemData: ItemData) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        products.forEach { item ->
            items(1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = item.name, style = TextStyle(fontSize = 24.sp))
                        Text(text = item.description, style = TextStyle(fontSize = 16.sp))
                    }
                    AddItemButton(item = item) { addItem(it) }
                }
            }
        }
    }
}
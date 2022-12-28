package com.example.bakeryapp.screen

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.nav.Screens
import com.example.bakeryapp.util.*

@Composable
fun MainScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity
){
    /** TOP BAR **/
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        /** Orders **/
        OutlinedButton(
            onClick = {
                if (AuthInfo.user != null) // only logged in users are allowed
                    navController.navigate(route = Screens.OrdersScreen.route)
                else Toast.makeText(mainActivity,
                    "Only logged in users are allowed to view this screen",
                    Toast.LENGTH_LONG).show()
            }
        ) {
            Text(text = "My Orders")
        }

        /** Cart **/
        OutlinedButton(
            onClick = {
                navController.navigate(route = Screens.CartScreen.route)
            }
        ) {
            Text(text = "Cart") //todo: make it an icon!
        }

        /** Login OR Sign-out **/
        AuthButton(
            navController = navController,
            sharedViewModel = sharedViewModel,
            mainActivity = mainActivity
        )

    }

    /** ITEMS **/
    Column(
        modifier = Modifier
            .padding(start = 50.dp, end = 50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /** User view */

        RenderItems(navController = navController, sharedViewModel = sharedViewModel)

        /* ADD ITEMS */
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = Screens.AddItemScreen.route)
            }
        ) {
            Text(text = "ADD Item Data")
        }

        /** Admin View */

        RenderMaterials(navController = navController, sharedViewModel = sharedViewModel)
    }


}

@Composable
private fun AuthButton(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity
){
    if (AuthInfo.user == null){
        Button(
            onClick = {
                navController.navigate(route = Screens.LoginScreen.route)
            }
        ){
            Text(text = "Login")
        }
    }
    else{
        Button(
            onClick = {
                sharedViewModel.signOut()
                mainActivity.reloadActivity()
            }
        ){
            Text(text = "Sign-Out")
        }
    }
}

@Composable
fun RenderItems(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    /**
     * Retrieves item data from firebase and composes it on screen
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

@Composable
fun RenderMaterials(
    navController: NavController,
    sharedViewModel: SharedViewModel
){
    var materials: List<MaterialsData> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(key1 = materials)
    {
        materials = sharedViewModel.getMaterials()
        materials.forEach { println(it) }
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

        itemHeader(text = "Materials")
        MaterialsList(materials)
    }
}

@Composable
fun MaterialsList(
    materials: List<MaterialsData>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        materials.forEach { item ->
            items(1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        Text(text = item.name, style = TextStyle(fontSize = 24.sp))
                        Text(text = item.description, style = TextStyle(fontSize = 16.sp))
                        Text(text = item.contactInfo, style = TextStyle(fontSize = 16.sp))
                    }
                }
            }
        }
    }
}
package com.example.bakeryapp.screen

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.R
import com.example.bakeryapp.nav.Screens
import com.example.bakeryapp.util.*
import com.example.bakeryapp.util.AuthInfo.isAdmin
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity,
) {
    isAdmin = remember { mutableStateOf(true) }
    LaunchedEffect(key1 = isAdmin)
    {
        if (AuthInfo.user == null) {
            isAdmin.value = false
        } else {
            var mid = sharedViewModel.checkAdmin(AuthInfo.user?.email.toString())
            if (mid is Boolean) {
                isAdmin.value = mid
            }
            Log.d("ISADMIN", isAdmin.toString())
        }
    }
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
                else Toast.makeText(
                    mainActivity,
                    "Only logged in users are allowed to view this screen",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            Text(text = "My Orders")
        }
        if (!isAdmin.value && FirebaseAuth.getInstance().currentUser != null) { // admins & unauthenticated users do not see cart button
            /** Cart **/
            OutlinedButton(
                onClick = {
                    navController.navigate(route = Screens.CartScreen.route)
                }
            ) {
                Text(text = "cart")
            }
        }

        /** Login OR Sign-out **/
        AuthButton(
            navController = navController,
            sharedViewModel = sharedViewModel,
            mainActivity = mainActivity
        )

    }
    Column(
        modifier = Modifier
            .padding(start = 25.dp, end = 25.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painterResource(R.drawable.logo),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(top = 32.dp)
        )
        /** Admin only View */
        if (isAdmin.value) {
            RenderMaterials(navController = navController, sharedViewModel = sharedViewModel)

            /* ADD ITEMS */
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate(route = Screens.AddItemScreen.route)
                }
            ) {
                Text(text = "ADD Item Data")
            }
        }
        /** User + Admin view */
        RenderItems(navController = navController, sharedViewModel = sharedViewModel)
    }
}

@Composable
private fun AuthButton(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity,
) {
    if (AuthInfo.user == null) {
        Button(
            onClick = {
                navController.navigate(route = Screens.LoginScreen.route)
            }
        ) {
            Text(text = "Login")
        }
    } else {
        Button(
            onClick = {
                sharedViewModel.signOut()
                mainActivity.reloadActivity()
            }
        ) {
            Text(text = "Sign-Out")
        }
    }
}

@Composable
fun RenderItems(
    navController: NavController,
    sharedViewModel: SharedViewModel,
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
        if (!isAdmin.value) { // admins do not see the menu
            itemHeader(text = "Menu")
            ProductItemList(products)
            { product -> cart = cart.addItemIncrease(product) }
        }
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
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(243, 226, 204, 255))
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (product in products) {
            item(span = {
                // LazyGridItemSpanScope:
                // maxLineSpan
                GridItemSpan(1)
            }) {
                Column {
                    Image(painterResource(id = R.drawable.productex1),
                        contentDescription = "",
                        modifier = Modifier
                            .width(75.dp)
                            .height(75.dp))
                    Row(verticalAlignment = CenterVertically) {
                        Column {
                            Text(text = product.name,
                                modifier = Modifier.padding(bottom = 2.dp),
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
                            Text(text = product.description,
                                style = TextStyle(fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray), modifier = Modifier.padding(2.dp))
                        }
                        if (FirebaseAuth.getInstance().currentUser != null) // only logged in accounts can add to cart
                            IconButton(onClick = {
                                addItem(product)
                            }) {
                                Image(painterResource(id = R.drawable.add),
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(20.dp)
                                        .padding(start = 8.dp),
                                    contentDescription = "Add")
                            }
                    }

                }
            }
        }

    }
}

@Composable
fun RenderMaterials(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
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
    materials: List<MaterialsData>,
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
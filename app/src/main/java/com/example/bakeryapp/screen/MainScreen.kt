package com.example.bakeryapp.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.nav.NavGraph
import com.example.bakeryapp.nav.Screens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(
    navController: NavController
){

    /** TOP BAR **/
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        /** Orders **/
        OutlinedButton(
            onClick = {
                navController.navigate(route = Screens.OrdersScreen.route)
            }
        ){
            Text(text = "My Orders")
        }

        /** Cart **/
        OutlinedButton(
            onClick = {
                navController.navigate(route = Screens.CartScreen.route)
            }
        ){
            Text(text = "Cart") //todo: make it an icon!
        }

        /** Login OR Sign-out **/
        Button(
            onClick = {
                navController.navigate(route = Screens.LoginScreen.route)
            }
        ){
            Text(text = "Login")
        }
    }

    /** ITEMS **/
    Column (
        modifier = Modifier
            .padding(start = 50.dp, end = 50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        /** GET ITEMS */
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = Screens.TEMPItemsScreen.route)
            }
        ){
            Text(text = "Get Item Data")
        }

        /** ADD ITEMS */
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = Screens.AddItemScreen.route)
            }
        ){
            Text(text = "ADD Item Data")
        }
    }
}
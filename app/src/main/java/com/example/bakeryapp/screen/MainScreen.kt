package com.example.bakeryapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.nav.Screens
import com.example.bakeryapp.util.AuthInfo
import com.example.bakeryapp.util.SharedViewModel

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
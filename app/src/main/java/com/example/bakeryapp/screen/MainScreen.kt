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
import com.example.bakeryapp.nav.Screens

@Composable
fun MainScreen(
    navController: NavController
){
    Column (
        modifier = Modifier
            .padding(start =50.dp, end = 50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        /* GET user button */
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = Screens.LoginScreen.route)
            }
        ){
            Text(text = "Get User Data")
        }

        /** ADD user button */
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = Screens.RegisterScreen.route)
            }
        ){
            Text(text = "Add User Data")
        }

        /** Logout Button */
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = Screens.RegisterScreen.route)
            }
        ){
            Text(text = "logout")
        }
    }
}
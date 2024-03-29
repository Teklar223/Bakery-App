package com.example.bakeryapp.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.screen.*
import com.example.bakeryapp.util.AuthInfo.isAdmin
import com.example.bakeryapp.util.SharedViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen.route
    ) {

        /** *** MAIN SCREEN *** **/
        composable(
            route = Screens.MainScreen.route
        ) {
            if (isAdmin.value == null)
                Text(text = "Loading...", modifier = Modifier.padding(16.dp))
            else MainScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                mainActivity = mainActivity,
                isAdmin = isAdmin.value!!
            )
        }

        /** *** LOGIN SCREEN *** **/
        composable(
            route = Screens.LoginScreen.route
        ) {
            LoginScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                mainActivity = mainActivity
            )
        }

        /** *** LOGIN SCREEN *** **/
        composable(
            route = Screens.RegisterScreen.route
        ) {
            RegisterScreen(
                navController = navController,
                sharedViewModel = sharedViewModel,
                mainActivity = mainActivity
            )
        }

        /** *** CART SCREEN *** **/
        composable(
            route = Screens.CartScreen.route
        ) {
            CartScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** ORDERS SCREEN *** **/
        composable(
            route = Screens.OrdersScreenAdmin.route
        ) {
            OrdersScreenAdmin(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** ORDERS SCREEN *** **/
        composable(
            route = Screens.OrdersScreen.route
        ) {
            OrdersScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** ADD ITEM SCREEN *** **/
        composable(
            route = Screens.AddItemScreen.route
        ) {
            AddItemScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        /** *** ADD MATERIAL SCREEN *** **/
        composable(
            route = Screens.AddMaterialScreen.route
        ) {
            AddMaterialScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
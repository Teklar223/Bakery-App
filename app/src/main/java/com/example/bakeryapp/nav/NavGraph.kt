package com.example.bakeryapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakeryapp.screen.*
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
){
    NavHost(
        navController = navController,
        startDestination = Screens.OrdersScreen.route
    ){
        /** *** TEMP ITEMS SCREEN *** **/
        composable( //TODO: remove when its done
            route = Screens.TEMPItemsScreen.route
        ){
            TEMPItemsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** MAIN SCREEN *** **/
        composable(
            route = Screens.MainScreen.route
        ){
            MainScreen(
                navController = navController
            )
        }

        /** *** LOGIN SCREEN *** **/
        composable(
            route = Screens.LoginScreen.route
        ) {
            LoginScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** CART SCREEN *** **/
        composable(
            route = Screens.CartScreen.route
        ){
            CartScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** ORDERS SCREEN *** **/
        composable(
            route = Screens.OrdersScreen.route
        ){
            OrdersScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }

        /** *** ADD ITEM SCREEN *** **/
        composable(
            route = Screens.AddItemScreen.route
        ){
            AddItemScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
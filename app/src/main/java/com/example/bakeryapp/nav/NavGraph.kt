package com.example.bakeryapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.screen.*
import com.example.bakeryapp.util.SharedViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity
){
    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen.route
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
                navController = navController,
                sharedViewModel = sharedViewModel,
                mainActivity = mainActivity
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

        /** *** GET MATERIALS SCREEN *** **/
        composable(
            route = Screens.MaterialsScreen.route
        ){
            MaterialsScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
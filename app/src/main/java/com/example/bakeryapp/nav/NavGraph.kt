package com.example.bakeryapp.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.screen.*
import com.example.bakeryapp.util.SharedViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
    }
}
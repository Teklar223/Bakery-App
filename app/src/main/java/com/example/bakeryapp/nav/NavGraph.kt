package com.example.bakeryapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakeryapp.screen.*
import com.example.bakeryapp.util.SharedViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
){
    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen.route
    ){
        /* *** MAIN SCREEN *** */
        composable(
            route = Screens.MainScreen.route
        ){
            MainScreen(
                navController = navController
            )
        }
        /* *** GET DATA SCREEN *** */
        composable(
            route = Screens.LoginScreen.route
        ){
            LoginScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        /* *** ADD DATA SCREEN *** */
        composable(
            route = Screens.RegisterScreen.route
        ){
            RegisterScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
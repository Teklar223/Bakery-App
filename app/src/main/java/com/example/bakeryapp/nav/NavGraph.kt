package com.example.bakeryapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bakeryapp.screen.*
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

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
            route = Screens.GetDataScreen.route
        ){
            GetDataScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        /* *** ADD DATA SCREEN *** */
        composable(
            route = Screens.AddDataScreen.route
        ){
            AddDataScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
    }
}
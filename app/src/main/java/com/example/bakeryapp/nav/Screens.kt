package com.example.bakeryapp.nav

sealed class Screens(val route: String){
    object MainScreen: Screens(route = "main_screen")
    object LoginScreen: Screens(route = "login_screen")
    object RegisterScreen: Screens(route = "register_screen")
}

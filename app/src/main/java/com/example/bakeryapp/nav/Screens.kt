package com.example.bakeryapp.nav

sealed class Screens(val route: String){
    object MainScreen: Screens(route = "main_screen")
    object LoginScreen: Screens(route = "login_screen")
    object CartScreen: Screens(route = "cart_screen")
    object OrdersScreen: Screens(route = "orders_screen")
    object AddItemScreen: Screens(route = "add_item_screen")
    /**
     * Login screen is merged with registration via the firebase auth' library
     * */
}

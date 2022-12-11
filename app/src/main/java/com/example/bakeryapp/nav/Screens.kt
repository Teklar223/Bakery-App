package com.example.bakeryapp.nav

sealed class Screens(val route: String){
    object MainScreen: Screens(route = "main_screen")
    object LoginScreen: Screens(route = "login_screen")
    object CartScreen: Screens(route = "cart_screen")
    object OrdersScreen: Screens(route = "orders_screen")
    object AddItemScreen: Screens(route = "add_item_screen")
    object TEMPItemsScreen: Screens(route = "temp_items_screen")
    object MaterialsScreen: Screens(route = "materials_screen")
    object TestCartOrdersScreen: Screens(route = "test_carts_orders")
    //TODO: remove 'TEMPItemsScreen' after it's dev' is done

    /**
     * Login screen is merged with registration via the firebase auth' library
     * */
}

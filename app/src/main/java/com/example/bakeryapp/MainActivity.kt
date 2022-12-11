package com.example.bakeryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.bakeryapp.nav.NavGraph
import com.example.bakeryapp.ui.theme.BakeryTheme
import com.example.bakeryapp.util.AuthInfo
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initiating auth
        AuthInfo.auth = FirebaseAuth.getInstance()
        //AuthInfo.auth.addAuthStateListener()
        AuthInfo.user = AuthInfo.auth.currentUser

        //initiating screens
        showMain()

    }

    private fun showMain() {
        setContent {
            BakeryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navController = rememberNavController()
                    sharedViewModel.setNav(navController)
                    //sharedViewModel.setActivity(this) // Bad practice
                    NavGraph(
                        navController = navController,
                        sharedViewModel = sharedViewModel,
                        mainActivity = this
                    )
                }
            }
        }
    }

    /** this acts as our way to 'refresh' the screens (for example - on sign-out)*/
    fun reloadActivity() {
        finish();
        //overridePendingTransition(0, 0);
        startActivity(intent);
        //overridePendingTransition(0, 0);
    }
}
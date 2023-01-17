package com.example.bakeryapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.example.bakeryapp.nav.NavGraph
import com.example.bakeryapp.ui.theme.BakeryTheme
import com.example.bakeryapp.util.AuthInfo
import com.example.bakeryapp.util.CartRepository
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/** An initiator class, in charge of starting the activity and init other needed objects */
class MainActivity : ComponentActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var navController: NavHostController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initiating auth
        AuthInfo.auth = FirebaseAuth.getInstance()
        AuthInfo.user = AuthInfo.auth.currentUser

        lifecycleScope.launch { /* preload cart */
            CartRepository.cart.value = CartRepository.getCart()
        }
        //initiating screens
        showMain()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMain() {
        setContent {
            BakeryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navController = rememberNavController()
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
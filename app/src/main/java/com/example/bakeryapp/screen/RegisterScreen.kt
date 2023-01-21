package com.example.bakeryapp.screen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.MainActivity
import com.example.bakeryapp.util.AuthInfo
import com.example.bakeryapp.util.LoadingState
import com.example.bakeryapp.util.SharedViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    mainActivity: MainActivity,
) {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    val state by sharedViewModel.loadingState.collectAsState()
    val scope = rememberCoroutineScope()

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                sharedViewModel.signWithCredential(
                    credential,
                    { mainActivity.reloadActivity() }, /* positive result */
                    {
                        Toast.makeText(mainActivity, "Google sign in failed", Toast.LENGTH_LONG)
                            .show() /* negative result */
                    })
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in CRITICAL failure", e)
            }
        }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    backgroundColor = Color.White,
                    elevation = 1.dp,
                    title = {
                        Text(text = "Register")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { AuthInfo.auth.signOut() }) {
                            Icon(
                                imageVector = Icons.Rounded.ExitToApp,
                                contentDescription = null,
                            )
                        }
                    }
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = userEmail,
                        label = {
                            Text(text = "Email")
                        },
                        onValueChange = {
                            userEmail = it
                        }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        value = userPassword,
                        label = {
                            Text(text = "Password")
                        },
                        onValueChange = {
                            userPassword = it
                        }
                    )

                    /** Register Button*/
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = userEmail.isNotEmpty() && userPassword.isNotEmpty(),
                        content = {
                            Text(text = "Register")
                        },
                        onClick = {
                            if (userPassword.length > 6) {
                                scope.launch {
                                    sharedViewModel.registerWithEmailAndPassword(
                                        email = userEmail.trim(),
                                        password = userPassword.trim(),
                                        { mainActivity.reloadActivity() }, /* positive result */
                                        {
                                            Toast.makeText(
                                                mainActivity,
                                                it.message,
                                                Toast.LENGTH_LONG
                                            )
                                                .show() /* negative result */
                                        }
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    mainActivity,
                                    "Password must be longer than 6 characters!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )

                    /** login Success/Failure message */
                    when (state.status) {
                        LoadingState.Status.SUCCESS -> {
                            Text(text = "Success")
                            navController.popBackStack()
                        }
                        LoadingState.Status.FAILED -> {
                            Text(text = state.msg ?: "Error")
                        }
                        else -> {}
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            )
        }
    )
}
package com.example.bakeryapp.screen

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.R
import com.example.bakeryapp.util.SharedViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.example.bakeryapp.util.AuthInfo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
){
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            //viewModel.signWithCredential(credential)
        } catch (e: ApiException) {
            //Log.w("TAG", "Google sign in failed", e)
        }
    }

    Scaffold(
    topBar = {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 1.dp,
                title = {
                    Text(text = "Login")
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

                Button(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = userEmail.isNotEmpty() && userPassword.isNotEmpty(),
                    content = {
                        Text(text = "Login")
                    },
                    onClick = {
                        //viewModel.signInWithEmailAndPassword(userEmail.trim(), userPassword.trim())
                    }
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    text = "Login with"
                )

                Spacer(modifier = Modifier.height(18.dp))

                val context = LocalContext.current
                val token = stringResource(R.string.default_web_client_id)

                OutlinedButton(
                    border = ButtonDefaults.outlinedBorder.copy(width = 1.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()

                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                Icon(
                                    tint = Color.Unspecified,
                                    painter = painterResource(id = com.firebase.ui.auth.R.drawable.googleg_standard_color_18),
                                    contentDescription = null,
                                )
                                Text(
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.onSurface,
                                    text = "Google"
                                )
                                Icon(
                                    tint = Color.Transparent,
                                    imageVector = Icons.Default.MailOutline,
                                    contentDescription = null,
                                )
                            }
                        )
                    }
                )
                /*
                when(state.status) {
                    LoadingState.Status.SUCCESS -> {
                        Text(text = "Success")
                    }
                    LoadingState.Status.FAILED -> {
                        Text(text = state.msg ?: "Error")
                    }
                    else -> {}
                }
                 */
            }
        )
    }
)
}



private fun startSignIn() {
    val providers = arrayListOf( //todo: should move to constants - if possible
        AuthUI.IdpConfig.EmailBuilder().build()/*,
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build()*/)

    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setTheme(R.style.Theme_BakeryApp)
        .setAvailableProviders(providers)
        .build()
    //launcher.launch(signInIntent)
}

/*
private val signInLauncher = registerForActivityResult(
    FirebaseAuthUIActivityResultContract()
) { result: FirebaseAuthUIAuthenticationResult? ->
    result.toString()
}

internal fun startSignIn() {
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build()/*,
        AuthUI.IdpConfig.PhoneBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build(),
        AuthUI.IdpConfig.TwitterBuilder().build()*/)

    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setTheme(R.style.Theme_BakeryApp)
        .setAvailableProviders(providers)
        .build()
    signInLauncher.launch(signInIntent)
}
*/
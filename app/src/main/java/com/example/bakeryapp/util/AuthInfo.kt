package com.example.bakeryapp.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * This object acts as a 'public vault' for the current auth information
 */
object AuthInfo {
    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null
    lateinit var isAdmin: MutableState<Boolean>
}
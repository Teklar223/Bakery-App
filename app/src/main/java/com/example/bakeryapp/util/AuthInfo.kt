package com.example.bakeryapp.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * This object acts as a 'public vault' for the current auth information
 */
object AuthInfo {

    lateinit var auth: FirebaseAuth
    var user: FirebaseUser? = null

}
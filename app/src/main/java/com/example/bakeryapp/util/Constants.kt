package com.example.bakeryapp.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


/* Firebase collection */

val database = Firebase.firestore
val dbRegion = "eur3"
val ordersCol = "orders"
val itemsCol = "items"
val materialsCol = "materials"

/* Common loading state class for observers */

data class LoadingState private constructor(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.RUNNING)
        fun error(msg: String?) = LoadingState(Status.FAILED, msg)
    }

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED,
        IDLE,
    }
}


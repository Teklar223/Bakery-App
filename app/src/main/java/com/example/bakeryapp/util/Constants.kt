package com.example.bakeryapp.util

//POTENTIAL TODO: make this something like a sealed class?
/* Firebase collection */
//val database = Firebase.firestore
//val dbRegion = "eur3"
const val ordersCol = "orders"
const val itemsCol = "items"
const val materialsCol = "materials"
const val cartsCol = "carts"

/* Business logic constants */
const val ordersToShow = 2

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


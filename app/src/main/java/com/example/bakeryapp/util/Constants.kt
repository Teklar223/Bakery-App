package com.example.bakeryapp.util

/* Data logic collection */
const val ordersCol = "orders"
const val itemsCol = "items"
const val materialsCol = "materials"
const val cartsCol = "carts"
const val adminsCol = "admins"

/* Business logic constants */
const val ordersToShow = 10

/* Common classes */

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

data class AdminData(
    var ID: String = ""
)

package com.example.bakeryapp.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.auth.api.Auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartRepository {

    /* Cart session object  */
    companion object {
        var cart: MutableState<Cart?> = mutableStateOf(null)

        /* clears session cart */
        fun clearSessionCart() {
            cart.value = Cart()
            cart.value?.userId = AuthInfo.user!!.uid
            cart.value?.save()
        }
        /** this method is in charge of syncing the cart object with the server */
        // encapsulates getCart() and saveNewCart()
        suspend fun getSessionCart(): Cart {
            if (cart.value == null) {
                AuthInfo.user?.let { user ->
                        cart.value = getCart()
                        if (cart.value == null) { // create new cart if doesn't exist
                            cart.value = Cart(items = mutableListOf(), userId = user.uid)
                            saveNewCart(cart.value!!)
                        }
                }
            }
            return cart.value ?: Cart()
        }

        // update user cart by user id
        suspend fun updateCart(fields: Map<String, Any>) {
            AuthInfo.user?.let { user ->
                withContext(Dispatchers.IO) { /* jump to IO thread */
                    Firebase.firestore
                        .collection(cartsCol)
                        .document(user.uid)
                        .update(fields)
                        .await()
                }
            } ?: run {
                println("User not logged in !")
            }
        }

        // set user cart by user id
        suspend fun saveNewCart(cart: Cart) {
            AuthInfo.user?.let { user ->
                withContext(Dispatchers.IO) { /* jump to IO thread */
                    Firebase.firestore
                        .collection(cartsCol)
                        .document(user.uid)
                        .set(cart)
                        .await()
                }
            } ?: run {
                println("User not logged in !")
            }
        }


        public suspend fun getCart(): Cart? {
            return AuthInfo.user?.let { user ->
                withContext(Dispatchers.IO) { /* jump to IO thread */
                    Firebase.firestore
                        .collection(cartsCol)
                        .document(user.uid)
                        .get()
                        .await()
                        .toObject(Cart::class.java)
                }
            } ?: run { null } /* user not logged in */
        }
    }

}
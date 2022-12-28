package com.example.bakeryapp.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartRepository {

    /* Cart session object  */
    companion object {
        var cart: Cart? = null

        /** this method is in charge of syncing the cart object with the server */
        // encapsulates getCart() and saveNewCart()
        suspend fun getSessionCart(): Cart {
            if (cart == null) {
                AuthInfo.user?.let { user ->
                    cart = getCart()  // get cart from db
                    if (cart == null) { // create new cart if doesn't exist
                        cart = Cart(items = mutableListOf(), userId = user.uid)
                        saveNewCart(cart!!)
                    }
                }
            }
            return cart ?: Cart()
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


        private suspend fun getCart(): Cart? {
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
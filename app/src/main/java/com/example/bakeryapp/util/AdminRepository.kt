package com.example.bakeryapp.util

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AdminRepository {

    suspend fun addItem(itemData:ItemData) {
        // upload item
        val newRef = FirebaseFirestore.getInstance()
            .collection("items")
            .add(itemData)
            .await()
        // set the item id (generated by firebase)
        newRef.update("itemId",newRef.id)
    }

    suspend fun addMaterial(material:MaterialsData) {
        // upload item
        val newRef = FirebaseFirestore.getInstance()
            .collection(materialsCol)
            .add(material)
            .await()
        // set the item id (generated by firebase)
        newRef.update("materialId", newRef.id)
    }
}
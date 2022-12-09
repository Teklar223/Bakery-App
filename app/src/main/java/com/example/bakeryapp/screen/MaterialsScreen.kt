package com.example.bakeryapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.util.ItemData
import com.example.bakeryapp.util.MaterialsData
import com.example.bakeryapp.util.SharedViewModel

@Composable
fun MaterialsScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
){
    /**
     * this screen is only for testing the ability to pull and layout items from the DB
     * and onto the screen during early dev' stages!
     * TODO: move all of these functionalities to MainScreen!
     */

    val context = LocalContext.current
    val materials: MutableState<List<MaterialsData>> = remember {
        mutableStateOf(listOf())
    }

    LaunchedEffect(key1 = materials) {
        val materialsData = sharedViewModel.getMaterials()
        materials.value = materialsData
    }

    Column(modifier = Modifier.fillMaxSize()) {
        /** Back button **/
        Row(
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ){
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
            }
        }

        /* @TODO Fetch items for each order list + Styling */
        for(material in materials.value) {
            Text(text = "Material name: ${material.materialId}")
            Row {
                Column {
                    Text(text = "Material Name: ${material.name}")
                    Text(text = "Material Cost:${material.cost}")
                    Text(text = "Material Currency: ${material.currency}")
                    Text(text = "Material Description: ${material.description}")
                }
            }
        }
    }
}
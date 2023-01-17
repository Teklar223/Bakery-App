package com.example.bakeryapp.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bakeryapp.R
import com.example.bakeryapp.util.ItemData
import com.example.bakeryapp.util.MaterialsData
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@Composable
fun AddMaterialScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    var isCurrencyMenuOpen by remember { mutableStateOf(false) }
    var materialName by remember { mutableStateOf("") }
    var materialUnit by remember { mutableStateOf("") }
    var materialCost by remember { mutableStateOf("") }
    var materialContactName by remember { mutableStateOf("") }
    var materialContactPhone by remember { mutableStateOf("") }

    var materialCurrency by remember { mutableStateOf("Shekel (₪)") }
    var materialDesc by remember { mutableStateOf("") }

    /** main layout **/
    Column(modifier = Modifier.fillMaxSize()) {
        /** Back button **/
        Row(
            modifier = Modifier
                .padding(start = 15.dp, top = 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "back button")
            }
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {


            AddMaterialTextField(name = "Material name", value = materialName) {
                materialName = it
            }
            AddMaterialTextField(name = "Material Description", value = materialDesc) {
                materialDesc = it
            }
            AddMaterialTextField(name = "Material Unit", value = materialUnit) {
                materialUnit = it
            }
            AddMaterialTextField(name = "Material Cost",
                textType = KeyboardType.Decimal,
                value = materialCost) {
                materialCost = it
            }
            AddMaterialTextField(name = "Contact Name",
                textType = KeyboardType.Text,
                value = materialContactName) {
                materialContactName = it
            }
            AddMaterialTextField(name = "Contact Phone",
                textType = KeyboardType.Phone,
                value = materialContactPhone) {
                materialContactPhone = it
            }

            CurrencySelectionList(
                isCurrencyMenuOpen = isCurrencyMenuOpen,
                selected = materialCurrency,
                setCurrencyMenuOpen = { isCurrencyMenuOpen = it },
                setSelected = { materialCurrency = it })
            Button(onClick = {
                sharedViewModel.addMaterial(
                    MaterialsData(
                        materialId = "",
                        name = materialName,
                        description = materialDesc,
                        cost = materialCost.toInt(),
                        currency = materialCurrency,
                        unit = materialUnit,
                        contactInfoName = materialContactName,
                        contactInfoPhone = materialContactPhone)
                )
                Toast.makeText(navController.context.applicationContext,
                    "Added Material successfully",
                    Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }) {
                Text("Add Material")
            }

        }

    }
}

@Composable
fun AddMaterialTextField(
    name: String,
    value: String,
    textType: KeyboardType = KeyboardType.Text,
    onChange: (str: String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.8f),
        value = value,
        keyboardOptions = KeyboardOptions(keyboardType = textType),
        label = {
            Text(text = "Enter ${name}")
        },
        onValueChange = {
            onChange(it)
        }
    )
}












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
import com.example.bakeryapp.util.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@Composable
fun AddItemScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {

    var isCurrencyMenuOpen by remember { mutableStateOf(false) }

    var itemName by remember { mutableStateOf("") }
    var itemDesc by remember { mutableStateOf("") }
    var itemCost by remember { mutableStateOf("") }
    var itemCurrency by remember { mutableStateOf("Shekel (₪)") }
    var itemImage by remember { mutableStateOf("https://i.ibb.co/wCxBgrs/removebg-preview.png") }

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

            GlideImage(
                imageModel = { itemImage },
                imageOptions= ImageOptions(contentScale = ContentScale.Fit),
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp))


            AddItemTextField(name = "Item name", value = itemName) {
                itemName = it
            }
            AddItemTextField(name = "Item Description", value = itemDesc) {
                itemDesc = it
            }
            AddItemTextField(name = "Item Cost",
                textType = KeyboardType.Decimal,
                value = itemCost) {
                itemCost = it
            }
            AddItemTextField(name = "Image URL",
                textType = KeyboardType.Text,
                value = itemImage) {
                itemImage = it
            }

            CurrencySelectionList(
                isCurrencyMenuOpen = isCurrencyMenuOpen,
                selected = itemCurrency,
                setCurrencyMenuOpen = { isCurrencyMenuOpen = it },
                setSelected = { itemCurrency = it })
            Button(onClick = {
                sharedViewModel.addItem(
                    ItemData(itemId = "", name = itemName, description = itemDesc,
                        cost = itemCost.toInt(), currency = itemCurrency,
                        image = itemImage)
                )
                Toast.makeText(navController.context.applicationContext,"Added item successfully",Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }) {
                Text("Add Item")
            }

        }

    }
}


@Composable
fun CurrencySelectionList(
    isCurrencyMenuOpen: Boolean,
    setCurrencyMenuOpen: (Boolean) -> Unit,
    selected: String,
    setSelected: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(top = 16.dp)
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(1.dp))
            .fillMaxWidth(0.8f)
    ) {
        // options button
        IconButton(onClick = {
            setCurrencyMenuOpen(true)
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Selected currency:", modifier = Modifier.padding(end = 8.dp))
                Text(style = TextStyle(fontWeight = FontWeight.Bold), text = selected)
            }
        }
        DropdownMenu(
            expanded = isCurrencyMenuOpen, onDismissRequest = {
                setCurrencyMenuOpen(false)
            }, modifier = Modifier.fillMaxWidth(0.8f)) {
            DropdownMenuItem(onClick = {
                setSelected("Dollar (\$)")
                setCurrencyMenuOpen(false)
            }) {
                Text("Dollar ($)")
            }
            DropdownMenuItem(onClick = {
                setSelected("Shekel (₪)")
                setCurrencyMenuOpen(false)
            }) {
                Text("Shekel (₪)")
            }
        }
    }
}

@Composable
fun AddItemTextField(
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
            if (textType == KeyboardType.Decimal &&  it.length > 5) return@OutlinedTextField
            onChange(it)
        }
    )
}












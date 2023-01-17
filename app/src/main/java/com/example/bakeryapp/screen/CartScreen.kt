package com.example.bakeryapp.screen

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.bakeryapp.util.*
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import java.time.LocalDateTime

@Composable
fun CartScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    var cart by remember { CartRepository.cart }
    var orderDate :LocalDateTime? = remember {  null }
    Row(
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (cart != null) {
            cartHeader(
                sharedViewModel = sharedViewModel,
                text = "Cart items",
                cart = cart!!
            )
            CartItemList(cart!!)
            { newCart ->
                CartRepository.cart.value = newCart
                cart = newCart
            }
            Text(
                text = "Order summary",
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(8.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Pick delivery date",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(8.dp)
                )
                CustomCalendarView(onDateSelected = { date ->
                    orderDate = date
                })
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(0.85f),
                    onClick = {
                        if(orderDate == null) {
                            Toast.makeText(navController.context, "Please select delivery date!", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        sharedViewModel.checkout(cart!!, orderDate!!)
                    },
                    interactionSource = MutableInteractionSource(

                    )
                ) {
                    Text(
                        text = "Submit Order",
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
            }

        }
    }
}


@Composable
fun CustomCalendarView(onDateSelected: (LocalDateTime) -> Unit) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(ContextThemeWrapper(context, android.R.style.Widget_CalendarView))
        },
        update = { view ->
            view.minDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000
            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDateTime
                        .now()
                        .withMonth(month + 1)
                        .withYear(year)
                        .withDayOfMonth(dayOfMonth)
                )
            }
        }
    )
}


@Composable
fun cartHeader(sharedViewModel: SharedViewModel, text: String, cart: Cart) {
    Text(
        modifier = Modifier.padding(6.dp),
        text = text,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    )

    Text(
        text = "Total Price:  ${cart.totalPrice} $",
        style = TextStyle(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(8.dp)
    )
}

/**  Cart List -  shows all items in the the current user's cart (LOGGED IN ONLY)  **/
@Composable
fun CartItemList(
    cart: Cart,
    update: (cart: Cart) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.35f)
            .fillMaxWidth()
            .border(border = BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        cart.items.forEach { cartItem ->
            items(1) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    GlideImage(imageModel = { cartItem.item.image },
                        modifier = Modifier
                            .width(70.dp)
                            .height(70.dp),
                        imageOptions = ImageOptions(contentScale = ContentScale.Fit))
                    Column(modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .offset(x = 16.dp)) {
                        Text(text = cartItem.item.name,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
                        Text(text = cartItem.item.description,
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray))
                        Text(
                            text = "Amount: ${cartItem.amount}",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                        )
                    }
                    _RemoveItemButton(item = cartItem) {
                        update(cart.removeCartItemDecrease(it))
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                    _AddItemButton(item = cartItem) {
                        update(cart.addCartItemIncrease(it))
                    }
                }
            }
        }
    }
}

@Composable
fun _RemoveItemButton(
    item: CartItem,
    removeItem: (item: CartItem) -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        modifier = Modifier.width(50.dp),
        onClick = {
            removeItem(item)
        }) {
        Text(
            text = "-",
            style = TextStyle(fontSize = 18.sp)
        )
    }
}

@Composable
fun _AddItemButton(
    item: CartItem,
    removeItem: (item: CartItem) -> Unit,
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        ),
        modifier = Modifier.width(50.dp),
        onClick = {
            removeItem(item)
        }) {
        Text(
            text = "+",
            style = TextStyle(fontSize = 18.sp)
        )
    }
}
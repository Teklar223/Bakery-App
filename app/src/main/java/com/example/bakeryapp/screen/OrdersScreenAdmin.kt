package com.example.bakeryapp.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bakeryapp.util.OrdersDataPopulated
import com.example.bakeryapp.util.SharedViewModel
import com.example.bakeryapp.util.minus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalUnit
import java.util.Calendar.DAY_OF_WEEK


fun dateAsString(date: LocalDateTime, full: Boolean = false): String {
    val dayS = ("0" + ((date.dayOfMonth + 1) % 31).toString())
    val day = dayS.slice(dayS.length - 2 until dayS.length)
    val monthS = ("0" + date.monthValue.toString())
    val month = monthS.slice(monthS.length - 2 until monthS.length)
    return if (full) "${day.minus(1)}-${month}-${date.year}" else "${day.minus(1)}-${month}"
}


@Composable
fun OrdersScreenAdmin(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val date = remember {
        LocalDateTime.now()
    }
    val orders: MutableState<List<OrdersDataPopulated>?> = remember {
        mutableStateOf(null)
    }
    /* runs only if 'orders' change'*/
    LaunchedEffect(key1 = orders) {
        val ordersData = sharedViewModel.getTodayOrders()
        orders.value = ordersData
    }
    Column {

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

    Column(modifier = Modifier.padding(16.dp)) {
        Text(style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
            text = "Orders - ${dateAsString(date)}")
        Spacer(modifier = Modifier.padding(16.dp))
        if (orders.value != null)
            OrderAdminList(orders.value!!)
    }
    }

}


@Composable
fun OrderAdminList(list: List<OrdersDataPopulated>) {
    val map = HashMap<String, Int>()

    list.forEach {
        it.list.forEach { item ->
            if (map.containsKey(item.item!!.name))
                map[item.item!!.name] = map[item.item!!.name]!!.plus(item.amount)
            else
                map[item.item!!.name] = item.amount
        }
    }

    Text(text = "Summary: ", style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold))
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
        .background(Color(255, 196, 0, 255))
        .padding(12.dp)
        .clip(RoundedCornerShape(8.dp))) {


        if (map.size < 1) Text(text = "No orders for today..") else map.map { (k, v) ->
            Text(text = k,
                color = Color.White,
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                fontSize = 20.sp)
            Text(text = "total: $v",
                color = Color.White,
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                fontSize = 12.sp)
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
    LazyColumn(content = {
        items(list, itemContent = { item ->
            Row(Modifier) {
                Column(Modifier
                    .border(BorderStroke(1.dp, color = Color.LightGray),
                        shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                ) {
                    Text(style = TextStyle(textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold), text = "Client name: ${item.userName}")
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(modifier = Modifier.padding(4.dp),
                        style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
                        text = "Items in order: ")
                    Column(modifier =
                    Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, color = Color.Black),
                            shape = RoundedCornerShape(5.dp))) {
                        item.list.map { orderItem ->
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(style = TextStyle(fontWeight = FontWeight.Bold),
                                    text = orderItem.item!!.name)
                                Text(style = TextStyle(fontSize = 12.sp),
                                    text = "Amount: ${orderItem.amount}")
                            }
                        }
                    }
                }
            }
        })
    })
}

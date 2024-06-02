package com.stormatte.tequbit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LearningHistory(){

    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val date = LocalDate.parse("24-05-2024", formatter)
    val learningHistory = listOf(
        LearningHistory("DSU","Lesson",date),
        LearningHistory("Min-Cut-Max-flow","Lesson",date.plusDays(1)),
        LearningHistory("DSU","Lesson",date.plusDays(2)),
        LearningHistory("DSU","Lesson",date.plusDays(2)),
    )

    Box(modifier= Modifier){
        Column(
            modifier = Modifier.padding(top=10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Icon(
                    imageVector = Icons.Sharp.KeyboardArrowLeft,
                    contentDescription = "New Chat Icon",
                    modifier = Modifier.clickable {  }
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = "Your Learning History")
                Spacer(modifier = Modifier.width(20.dp))

                Icon(
                    imageVector = Icons.Sharp.Settings,
                    contentDescription = "New Chat Icon",
                    modifier = Modifier.clickable {  }
                )

            }
            //TextField Line
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .shadow(4.dp, ambientColor = Color(40000000))
                    .padding(top = 30.dp, bottom = 50.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                trailingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search Icon")
                }
            )
            Column(
                modifier = Modifier.padding(top = 30.dp, bottom = 50.dp, start = 20.dp, end = 20.dp),

            ) {
                //for lessons using lazy grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .height(350.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(37.dp,)
                ) {
                    items(learningHistory.size) {
                        LearningHistoryCard(index = it, name = learningHistory[it].lesson, type =learningHistory[it].type , date = learningHistory[it].date)

                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ElevatedButton(
                modifier = Modifier
                    .height(70.dp)
                    .width(270.dp),
                shape = RoundedCornerShape(20.dp),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Start a New Learning Session",
                    color = if(isSystemInDarkTheme()){
                        Color.White
                    }else{
                        Color.Black
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                Icon(imageVector = Icons.Sharp.Add, contentDescription = "New Chat Icon")
            }
        }
    }
}

@Composable
fun LearningHistoryCard(index:Int,name:String,type:String,date:LocalDate){

    val color1 = randomColor()
    val color2 = randomColor()

    ElevatedButton(
        modifier = Modifier
            .fillMaxWidth(),
//            .height(56.dp),
        onClick = {},
        shape = RoundedCornerShape(20.dp)
    ) {
            Text(

                text = "L${index+1}",
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(
                               color1,
                                color2

                            )
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(10.dp),
                color = if(isSystemInDarkTheme()){
                    Color.White
                }else{
                    Color.Black
                }


            )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = "$name - $type - $date",
            modifier = Modifier
                .width(250.dp)
                .padding(start = 20.dp),
            fontSize = 17.sp,
            color = if(isSystemInDarkTheme()){
                Color.White
            }else{
                Color.Black
            }
        )

    }
}

@Composable
fun IconFunction(){
    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search Icon")
}
//fun colorString():String{
//    var src = "0123456789abcdef0123456789abcdef0123456789abcdef"
//
//    var finalString ="0x"
//
//    for(i in 1..3){
//        finalString+=src[ (0..src.length).random()]
//        finalString+=src[ (0..src.length).random()]
//    }
//    return finalString
//}

fun randomColor(): Color {
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color(red, green, blue)
}

data class LearningHistory(val lesson:String, val type:String,val date:LocalDate)
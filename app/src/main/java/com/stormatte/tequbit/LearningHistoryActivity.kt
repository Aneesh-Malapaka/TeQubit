package com.stormatte.tequbit

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

suspend fun populateLearningHistory(learningHistory: MutableList<LearningHistoryItem>){
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    val learningHistories = Firebase.database.getReference("lessons/$userId").get().await()
//    println("$userId and $learningHistories")
    for(item in learningHistories.children){
        val lastItem = item.child((item.childrenCount - 1).toString()).value as Map<String, String>
        var itemTitle = ""
        if(lastItem["sender"] == "AI"){
           val itemMap = parseResponse(lastItem["message"]!!)
            itemTitle = itemMap["RESPONSE_TITLE"].toString() ?: itemMap["LESSON_TITLE"].toString()
        }
        val itemId = item.key!!
        learningHistory.add(LearningHistoryItem(itemTitle, itemId))
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LearningHistory(navToNext: (destination:String, chatId: String) -> Unit,viewModel: QubitViewModel){
    val originalLearningHistory = remember { mutableStateListOf<LearningHistoryItem>() }
    val displayedLearningHistory = remember { mutableStateListOf<LearningHistoryItem>() }

    LaunchedEffect(Unit) {
        if (originalLearningHistory.isEmpty()) {
            populateLearningHistory(originalLearningHistory)
            displayedLearningHistory.addAll(originalLearningHistory)
        }
    }

    var searchTerm by remember { mutableStateOf("") }
    val darkTheme = viewModel.darkTheme.value

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
                    imageVector = Icons.AutoMirrored.Sharp.KeyboardArrowLeft,
                    contentDescription = "Back to Home Icon",
                    modifier = Modifier
                        .clickable { }
                        .width(35.dp)
                        .height(35.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = "Your Learning History")
                Spacer(modifier = Modifier.width(20.dp))

                Icon(
                    imageVector = Icons.Sharp.Settings,
                    contentDescription = "Settings Icon",
                    modifier = Modifier
                        .clickable {
                            navToNext("settings", "")
                        }
                        .width(30.dp)
                        .height(30.dp)
                )

            }

            Spacer(modifier = Modifier.height(20.dp))
            //TextField Line
            OutlinedTextField(
                value = searchTerm,
                onValueChange = {
                    //programming fact: the basics matter - an empty string is a substring of every string - don't remove this comment
                    searchTerm = it
                    val filteredHistory = searchHistory(originalLearningHistory, it)
                    displayedLearningHistory.clear()
                    displayedLearningHistory.addAll(filteredHistory)
                },
                modifier = Modifier
//                    .shadow(4.dp, ambientColor = Color(40000000))
                    .padding(top = 10.dp)
                    .width(320.dp),
                trailingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search Icon")
                },
                shape = RoundedCornerShape(26.dp)
            )
            Column(
                modifier = Modifier.padding(top = 30.dp, bottom = 50.dp, start = 20.dp, end = 20.dp),

            ) {
                //for lessons using lazy grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .height(390.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(35.dp,)
                ) {
                    items(displayedLearningHistory.size) {
                        LearningHistoryCard(index = it, lesson = displayedLearningHistory[it], navToNext,darkTheme)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            ElevatedButton(
                modifier = Modifier
                    .height(70.dp)
                    .width(270.dp),
                shape = RoundedCornerShape(20.dp),
                onClick = {
                    navToNext("",generateChatID())
                }
            ) {
                Text(
                    text = "Start a New Learning Session",
                    color = if(darkTheme){
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
fun LearningHistoryCard(index:Int,lesson: LearningHistoryItem, navToNext: (destination:String,chatId: String) -> Unit,darkThemeValue:Boolean){

    val color1 = randomColor()
    val color2 = randomColor()

    ElevatedButton(
        modifier = Modifier
            .fillMaxWidth(),
//            .height(56.dp),
        onClick = {
            navToNext("",lesson.chatID)
        },
        elevation = ButtonDefaults.elevatedButtonElevation(
            4.dp
        ),
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
                color = if(darkThemeValue){
                    Color.White
                }else{
                    Color.Black
                }


            )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = lesson.lesson,
            modifier = Modifier
                .width(250.dp)
                .padding(start = 20.dp),
            fontSize = 17.sp,
            color = if(darkThemeValue){
                Color.White
            }else{
                Color.Black
            }
        )

    }
}

fun searchHistory(learningHistory: SnapshotStateList<LearningHistoryItem>, searchTerm:String) : SnapshotStateList<LearningHistoryItem>{
    val filteredHistory = learningHistory.filter {
        it.lesson.contains(searchTerm, ignoreCase = true)
    }

    val result = SnapshotStateList<LearningHistoryItem>()
    result.addAll(filteredHistory)
    return result
}
@Composable
fun IconFunction(){
    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search Icon")
}
fun randomColor(): Color {
    val red = (0..255).random()
    val green = (0..255).random()
    val blue = (0..255).random()
    return Color(red, green, blue)
}

data class LearningHistoryItem(val lesson:String, val chatID: String)
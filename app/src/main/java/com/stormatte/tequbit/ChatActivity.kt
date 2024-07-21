package com.stormatte.tequbit

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.stormatte.tequbit.ui.theme.DarkIconsText
import com.stormatte.tequbit.ui.theme.LightIconsText

// use it for viewModel, (stackoverflow - https://stackoverflow.com/questions/72541475/how-to-add-more-items-to-a-static-list-in-jetpack-compose)
//val _noteList = remember { MutableStateFlow(listOf<String>()) }
//val noteList by remember { _noteList }.collectAsState()
//
//// Add note
//fun addItem(item: String) {
//    val newList = ArrayList(noteList)
//    newList.add(yourItem)
//    _noteList.value = newList
//}
@Composable
fun LessonChat(chatViewModel: LessonChatWrapper,chatID:String, navBack: () -> Unit,navController: NavHostController) {
    val darkTheme = isSystemInDarkTheme()
    println("The chat id received is $chatID")
    LaunchedEffect(chatID) {
        chatViewModel.setChatID(chatID)
        chatViewModel.initializeChat()
    }
    LaunchedEffect(chatViewModel.messages.size,chatID){
        println("Asking Gemini")
        try{
            chatViewModel.askGemini()
            if(chatViewModel.messages.size != 0 && chatViewModel.messages.last().parsedMessage != null && chatViewModel.messages.last().parsedMessage!!["meta"] == "RESPONSE"){
                chatViewModel.setChatTitle(chatViewModel.messages.last().parsedMessage!!["RESPONSE_TITLE"]!!)
            }
        }
        catch (it: Exception) {
            // TODO: Sometimes api just errors out. Maybe add a retry button? For now I'm gonna leave it, as it only occurs at the start of the app
            Log.e(BuildConfig.APPLICATION_ID, it.stackTraceToString())
        }
    }
    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.KeyboardArrowLeft,
                    contentDescription = "Back To Home",
                    modifier = Modifier
                        .clickable {
                            navBack()
                        }
                        .width(35.dp)
                        .height(35.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = chatViewModel.chatTitle.value)
                Spacer(modifier = Modifier.width(20.dp))

                Icon(
                    imageVector = Icons.Sharp.Settings,
                    contentDescription = "Settings Icon",
                    modifier = Modifier
                        .clickable {

                        }
                        .width(35.dp)
                        .height(35.dp)
                )

            }
            val listState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                contentPadding = PaddingValues(vertical = 10.dp),
                state = listState
            ) {
                items(chatViewModel.messages.size) { message ->
                    if(chatViewModel.messages[message].type == "Input") {
                        if (chatViewModel.messages[message].sender == SenderType.AI) {
                            MessageDisplay(
                                index = message,
                                senderType = chatViewModel.messages[message].sender,
                                parsedMessage = chatViewModel.messages[message].parsedMessage!!,
                                navController
                            )
                        } else {
                            MessageDisplay(
                                index = message,
                                senderType = chatViewModel.messages[message].sender,
                                parsedMessage = chatViewModel.messages[message].message,
                                navController
                            )

                        }
                    }
                    LaunchedEffect(message){
                        if(message == (chatViewModel.messages.size-1)){
                            listState.animateScrollToItem(message)
                        }
                    }

                }
            }
            val textField  = chatViewModel.textFieldVal
            //TextField Line
            OutlinedTextField(
                value = textField.value,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if(darkTheme) DarkIconsText else LightIconsText
                ),
                maxLines = 4,
                singleLine = false,
                onValueChange = {
                    textField.value = it
                },
                modifier = Modifier
//                    .shadow(2.dp, ambientColor = Color(40000000))
                    .padding(top = 5.dp, bottom = 3.dp)
                    .width(320.dp)
//                    .height(50.dp)
                    .clickable {
                        chatViewModel.messages.add(
                            MessageFormat(
                                "Input",
                                SenderType.USER,
                                textField.value,
                                null
                            )
                        )
                        textField.value = ""
                    },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Send Chat Icon",
                        modifier = Modifier
                            .conditional(darkTheme,
                                ifTrue = {
                                    background(
                                        Color(0x51D3CDCD),
                                        RoundedCornerShape(30.dp)
                                    )
                                },
                                ifFalse = {
                                    background(
                                        Color(0xE1000000),
                                        RoundedCornerShape(30.dp)
                                    )
                                }
                            )
                            .width(35.dp)
                            .height(35.dp)
                            .clickable {
                                chatViewModel.messages.add(
                                    MessageFormat(
                                        "Input",
                                        SenderType.USER,
                                        textField.value,
                                        null
                                    )
                                )
                                textField.value = ""
                            },
                        tint = Color.White,
                    )
                },
                shape = RoundedCornerShape(26.dp)
            )
        }
    }
}
@Composable
fun MessageDisplay(index: Int, senderType: Enum<SenderType>, parsedMessage: Map<String, String>,navController: NavHostController){
    var chat = ""
    val lessonArray:MutableList<Map<String,String>> = mutableListOf()
    if(parsedMessage["meta"] == "RESPONSE"){
        chat = parsedMessage["RESPONSE_BODY"]!!
    }
    else if(parsedMessage["meta"] == "LESSON"){
        chat = parsedMessage["LESSON_INFO"].toString()
    }
    else{
        lessonArray.toMutableList().add(parsedMessage)
    }
    return MessageDisplay(index, senderType, chat, navController, metaType = parsedMessage["meta"].toString(),lessonArray)
}

@Composable
fun MessageDisplay(index: Int, senderType: Enum<SenderType>, parsedMessage: String,  navController: NavHostController, metaType:String="", lesson:MutableList<Map<String,String>> = mutableListOf() ) {

    val gson = remember { Gson() }
    val isSenderAI: Boolean = senderType == SenderType.AI
    var chat = parsedMessage


    val color1 = Color(0xD296C218)
    val color2 = Color(0xD218C21B)
    val color3 = Color(0xD218C2C2)
    val color4 = Color(0xD21851C2)

    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .conditional(
                isSenderAI,
                ifTrue = { background(Color(0x40000000), shape = RoundedCornerShape(20.dp)) },
                ifFalse = {
                    background(
                        Color(0x70000000),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            )
            .padding(10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = if (isSenderAI) Arrangement.Start else Arrangement.End
    ) {

        if (isSenderAI) {
            Text(
                text = "AI",
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
                    .padding(10.dp)
                    .width(20.dp)
                    .height(20.dp),

                color = if (isSystemInDarkTheme()) {
                    Color.White
                } else {
                    Color.Black
                },
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(0.dp))
            Column {
                Text(
                    text = chat,
                    modifier = Modifier
                        .width(250.dp)
//                        .wrapContentWidth()
                        .padding(start = 20.dp, top = 6.dp),
                    fontSize = 17.sp,
                    color = if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    textAlign = TextAlign.Justify,
                    lineHeight = 30.sp
                )
                if(metaType=="LESSON")
                Column {
                    ElevatedButton(onClick = {
                        val lessonJson = gson.toJson(lesson)
//                        navController.navigate("lesson_window/$lessonJson")
                        navController.navigate("lesson_window")
                    }) {
                        Text(text = "Start Lesson Now")
                    }
                }
            }

        } else {
            Row(
                modifier = Modifier.padding(top=8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat,
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentWidth(Alignment.End)
                        .padding(start = 30.dp),
                    fontSize = 17.sp,
                    color = if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    textAlign = TextAlign.Justify,
                    lineHeight = 30.sp,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "U",
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(
                                color3,
                                color4
                            )
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(10.dp)
                    .width(20.dp)
                    .height(20.dp),

                color = if (isSystemInDarkTheme()) {
                    Color.White
                } else {
                    Color.Black
                },
                textAlign = TextAlign.Center
            )
        }
    }
    Spacer(modifier =Modifier.height(15.dp))
}

data class MessageFormat(val type: String, val sender: Enum<SenderType>, var message: String, var parsedMessage: Map<String, String>?)

enum class SenderType {
    AI,
    USER
}

//adding a custom Modifier function for different conditions
fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: (Modifier.() -> Modifier)? = null,
): Modifier {
    return if (condition) {
        then(ifTrue(Modifier))
    } else if (ifFalse != null) {
        then(ifFalse(Modifier))
    } else {
        this
    }
}
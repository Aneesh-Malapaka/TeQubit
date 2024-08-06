package com.stormatte.tequbit

import android.util.Log
import android.view.View
import android.widget.TextView
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.stormatte.tequbit.ui.theme.DarkIconsText
import com.stormatte.tequbit.ui.theme.LightIconsText
import io.noties.markwon.Markwon


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
fun LessonChat(chatViewModel: LessonChatWrapper,chatID:String,navController: NavHostController,viewModel: QubitViewModel) {
    val darkTheme = viewModel.darkTheme.value
    println("The chat id received is $chatID")
    LaunchedEffect(chatID) {
        chatViewModel.setChatID(chatID)
        chatViewModel.initializeChat()
    }
    LaunchedEffect(chatViewModel.messages.size,chatID){
        println("Asking Gemini")
        try{
            chatViewModel.askGemini()
            if(chatViewModel.messages.size != 0 && chatViewModel.messages.last().parsedMessage != null && chatViewModel.messages.last().parsedMessage!!.isNotEmpty()){
                chatViewModel.setChatTitle(chatViewModel.messages.last().parsedMessage!![0].values.first().keys.first())
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
                            navController.navigate("home_screen") {
                                popUpTo(navController.graph.id)
                            }
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
                            navController.navigate("settings")
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
                                navController,
                                darkTheme
                            )
                        } else {
                            MessageDisplay(
                                index = message,
                                senderType = chatViewModel.messages[message].sender,
                                chat = chatViewModel.messages[message].message,
                                navController,
                                darkTheme = darkTheme
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
fun MessageDisplay(index: Int, senderType: Enum<SenderType>, parsedMessage: List<Map<String, Map<String, String>>>,navController: NavHostController, darkTheme: Boolean){
    var chat = ""
    var meta = ""

    if(parsedMessage[0].containsKey("RESPONSE")){
        chat = parsedMessage[0]["RESPONSE"]!!.values.first()
        meta = "RESPONSE"
    }
    else if(parsedMessage[0].containsKey("LESSON")){
        chat = parsedMessage[0]["LESSON"]!!.values.first()
        meta = "LESSON"
    }
    else if(parsedMessage[0].containsKey("ERROR")){
        chat = parsedMessage[0]["ERROR"]!!.values.first()
        meta = "ERROR"
    }
    return MessageDisplay(
        index = index,
        senderType = senderType,
        chat = chat,
        navController = navController,
        metaType = meta,
        lesson = parsedMessage,
        darkTheme
    )
}

@Composable
fun MessageDisplay(
    index: Int,
    senderType: Enum<SenderType>,
    chat: String,
    navController: NavHostController,
    metaType:String="",
    lesson:List<Map<String, Map<String,String>>> = mutableListOf(),
    darkTheme: Boolean
){
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }
    val isSenderAI: Boolean = senderType == SenderType.AI
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

                color = if (darkTheme) {
                    Color.White
                } else {
                    Color.Black
                },
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(0.dp))
            Column {
//                Text(
//                    text = chat,
//                    modifier = Modifier
//                        .width(250.dp)
////                        .wrapContentWidth()
//                        .padding(start = 20.dp, top = 6.dp),
//                    fontSize = 17.sp,
//                    color = if (isSystemInDarkTheme()) {
//                        Color.White
//                    } else {
//                        Color.Black
//                    },
//                    textAlign = TextAlign.Justify,
//                    lineHeight = 30.sp
//                )
                MarkdownTextChat(markdown = chat, markwon = markwon, darkTheme = darkTheme)
                if(metaType=="LESSON")
                Column {
                    ElevatedButton(onClick = {
                        val lesson_body = mutableListOf<Map<String, Map<String, String>>>()
                        for(i in 2..< lesson.size){
                            lesson_body.add(lesson[i])
                        }
                        navController.currentBackStackEntry?.savedStateHandle?.set("lesson_data", lesson_body)
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
                        .padding(start = 30.dp, top = 0.dp),
                    fontSize = 17.sp,
                    color = if (darkTheme) {
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

                color = if (darkTheme) {
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

data class MessageFormat(val type: String, val sender: Enum<SenderType>, var message: String, var parsedMessage: List<Map<String, Map<String, String>>>?)

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

//creating a seperate Markdown for chat window because of the different styling of lesson and chat
@Composable
fun MarkdownTextChat(markdown: String, modifier: Modifier = Modifier, markwon: Markwon, darkTheme:Boolean) {

    AndroidView(
        factory = { context ->
            TextView(context).apply {
                textSize = 17f // in SP
                if(darkTheme){
                    setTextColor(ContextCompat.getColor(context,R.color.whitePure))
                }
                setLineSpacing(5.2f,1.3f)
                textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                width = 250
                setPadding(20,6, 20, 6)
            }
        },
        update = { textView ->
            markwon.setMarkdown(textView, markdown)
        },
        modifier = modifier.fillMaxWidth()
    )
}
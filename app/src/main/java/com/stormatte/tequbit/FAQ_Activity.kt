package com.stormatte.tequbit

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FAQ_Screen(viewModel: QubitViewModel) {

    val questionAnswers = listOf(
        FAQ(
            "What can TeQubit Do?",
            "TeQubit can help you solve your programming and other computer science queries in the way you would prefer based on your existing knowledge. I can generate Lessons, a special feature of TeQubit where it will divide the explanation into sections."
        ),
        FAQ(
            "How can I generate a Lesson?",
            "You can use the command [LESSON] followed by your query to generate a lesson, but sometimes TeQubit can also generate a Lesson without command too based on the response it makes in the backend."
        ),
        FAQ(
            "How can I customize TeQubit?",
            "At any moment you can change how TeQubit should answer to you. It can be set from the 'Update Preferences' Section in the settings screen. The preferences are mapped in such a way that TeQubit will have a preset context based on your selection of knowledge level and response ways."
        ),
        FAQ(
            "What is the range of TeQubit in Computer Sciences Knowledge?",
            "Since we are using the Gemini Model and our own prompt engineering we have customized the responses to answer anything in computer science and other science topics. While we can't always guarantee the expertise in other domains, we have coded to avoid wrong information/out-of-topic questions."
        ),
        FAQ(
            "What other features can I expect soon?",
            "Once the app releases and gains users, we have planned to release customised experience for QUIZ and Mock Interview Experience. Along with that, we will experiment with Image Recognition to help coder solve their coding queries and eliminate the need for manual input of code. We would also implement few updates for the UI."
        ),
        FAQ(
            "Where can I leave a feedback?",
            "We would always love a feedback, be it for the UI or the Content. You can give us a feedback on playstore or you can personally send a feedback from the app itself, located in the settings screen."
        ),
    )

    Box(modifier = Modifier) {
        Column(
            modifier = Modifier.padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 18.sp, fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.padding(20.dp)
            ) {
               items(count = 1, itemContent = {
                   questionAnswers.forEachIndexed { index, item->
                       Text(
                           text = "${index+1}: ${item.question}",
                           fontSize = 20.sp,
                           fontFamily = FontFamily.Monospace,
                           fontWeight = FontWeight.Bold,
                           lineHeight = 35.sp,
                           modifier = Modifier.padding(bottom = 10.dp)
                       )
                       Text(
                           text = item.answer,
                           textAlign = TextAlign.Justify,
                           softWrap = true,
                           lineHeight = 30.sp,
                           fontSize = 18.sp,
                           modifier = Modifier.padding(top = 10.dp, bottom = 50.dp)
                       )
                   }
               })
            }
        }
    }
}

data class FAQ(val question:String, val answer:String)
package com.stormatte.tequbit

import android.view.View
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.noties.markwon.Markwon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LessonsWindow(lesson: MutableList<Map<String, String>>, viewModel: QubitViewModel) {
    val context = LocalContext.current
    //lesson structure is as follows
    // listOf(mapOf(meta to mapOf(section_title to section_content))
    val darkTheme = viewModel.darkTheme.value
    val lesson = mutableListOf(
        mapOf(
            "INTRODUCTION" to
                    mapOf(
                        "Understanding the Challenge|" to "The issue you're facing is common when working with dynamic lists in UI development. When new items are added to a list (especially at the beginning), the visible area of the list might jump unexpectedly due to how scrolling and item positioning are handled. `LazyListState` in Jetpack Compose provides the tools you need to control this behavior and ensure smooth, predictable scrolling."
                    )
        ),
        mapOf(
            "DESCRIPTION" to
                    mapOf(
                        "Using LazyListState for Smooth Scrolling" to "Here's a breakdown of how to use `LazyListState` to achieve smooth scrolling to the latest message:\n" +
                                "\n" +
                                "**1. Accessing `LazyListState`:**\n" +
                                "\n" +
                                "First, you need to get a reference to the `LazyListState` object associated with your `LazyColumn` or `LazyRow`. You can do this using the `rememberLazyListState()` function:\n" +
                                "\n" +
                                "```kotlin\n" +
                                "val listState = rememberLazyListState()\n" +
                                "\n" +
                                "LazyColumn(\n" +
                                " state = listState, // Attach the state\n" +
                                " // ... your other LazyColumn parameters\n" +
                                ") /* ... */ \n" +
                                "```\n" +
                                "\n" +
                                "**2. Deciding When to Scroll:**\n" +
                                "\n" +
                                "You'll need to determine when you want to scroll to the latest message. Typically, this happens when:\n" +
                                "\n" +
                                "* A new message is added to your data source.\n" +
                                "* The user opens the chat screen and you want to show the bottom of the conversation.\n" +
                                "\n" +
                                "**3. Smooth Scrolling with `animateScrollToItem`:**\n" +
                                "\n" +
                                "The `animateScrollToItem` function is key here. It lets you programmatically scroll to a specific item in the `LazyList` with a smooth animation. \n" +
                                "\n" +
                                "```kotlin\n" +
                                "// Assuming 'messages' is your list of messages\n" +
                                "listState.animateScrollToItem(index = messages.size - 1) \n" +
                                "```\n" +
                                "\n" +
                                "**Important:** You should call `animateScrollToItem` within a `LaunchedEffect` to ensure it runs after the composition (and potential list updates) have happened:\n" +
                                "\n" +
                                "```kotlin\n" +
                                "LaunchedEffect(key1 = messages) // Trigger on messages update \n" +
                                " listState.animateScrollToItem(index = messages.size - 1)\n" +
                                "```\n" +
                                "\n" +
                                "**4. Handling Potential Race Conditions:**\n" +
                                "\n" +
                                "Sometimes, there might be a slight delay between when you add a new message to your list and when the `LazyList` is fully recomposed. This can lead to the scrolling animation not working as expected. To address this, you can use `scrollToItem` which immediately scrolls to the item without animation, or you can introduce a slight delay before scrolling.\n" +
                                "\n" +
                                "```kotlin\n" +
                                "// Option 1: Immediate scroll (no animation)\n" +
                                "listState.scrollToItem(index = messages.size - 1) \n" +
                                "\n" +
                                "// Option 2: Introduce a delay\n" +
                                "LaunchedEffect(key1 = messages) \n" +
                                " delay(100) // Adjust delay if needed \n" +
                                " listState.animateScrollToItem(index = messages.size - 1)\n" +
                                "```\n" +
                                "\n" +
                                "**5. Avoiding Glitches with Item Keys:**\n" +
                                "\n" +
                                "Make sure you're providing unique keys to the items in your `LazyColumn` using the `key` parameter within your `items` block. This helps Jetpack Compose efficiently update the list and prevents unexpected behavior when items are added or removed.\n" +
                                "```kotlin\n" +
                                "LazyColumn \n" +
                                " items(messages, key = message -> message.id ) message -> \n" +
                                " // ... your message composable\n" +
                                "```\n" +
                                "\n" +
                                "By carefully managing your `LazyListState`, you can ensure that your chat window scrolls smoothly to the latest message without any frustrating glitches!"
                    )
        ),
        mapOf(
            "APPLICATION" to
                    mapOf(
                        "Putting It Together: Example|" to "Here's a more complete example of how you might implement smooth scrolling in a chat app using Jetpack Compose:\n" +
                                "```kotlin\n" +
                                "@Composable\n" +
                                "fun ChatScreen(messages: List<Message>) {\n" +
                                " val listState = rememberLazyListState()\n" +
                                "\n" +
                                " // Assuming you have a function to add a new message\n" +
                                " var newMessageText by remember { mutableStateOf(\\\"\\\") }\n" +
                                "\n" +
                                " Column {\n" +
                                " // Display chat messages\n" +
                                " LazyColumn(\n" +
                                " state = listState,\n" +
                                " modifier = Modifier.weight(1f), // Take up remaining space\n" +
                                " reverseLayout = true // Display messages from bottom to top\n" +
                                " ) {\n" +
                                " items(messages, key = { message -> message.id }) { message ->\n" +
                                " MessageBubble(message = message)\n" +
                                " }\n" +
                                " }\n" +
                                "\n" +
                                " // Input field for new messages\n" +
                                " Row(modifier = Modifier.padding(8.dp)) {\n" +
                                " TextField(\n" +
                                " value = newMessageText,\n" +
                                " onValueChange = { newMessageText = it },\n" +
                                " placeholder = { Text(\\\"Enter a message\\\") },\n" +
                                " modifier = Modifier.weight(1f)\n" +
                                " )\n" +
                                " Button(onClick = { /* Add new message to 'messages' */ }) {\n" +
                                " Text(\\\"Send\\\")\n" +
                                " }\n" +
                                " }\n" +
                                " }\n" +
                                "\n" +
                                " // Smooth scroll to the latest message whenever the 'messages' list changes\n" +
                                " LaunchedEffect(key1 = messages) {\n" +
                                " listState.animateScrollToItem(index = 0) // In a reversed layout, the latest message is at index 0\n" +
                                " }\n" +
                                "}\n" +
                                "```\n" +
                                "Remember to adapt this example to your specific app's data structures and message UI. With a bit of practice, you'll be able to create chat experiences that are both visually appealing and functionally sound!"
                    )
        ),
        mapOf(
            "SUMMARY" to
                    mapOf(
                        "Smooth Scrolling Mastered|" to "By combining `LazyListState`, `animateScrollToItem`, and some careful timing considerations, you can achieve smooth and user-friendly scrolling behavior in your Jetpack Compose apps. Make sure to test your implementation thoroughly, especially when adding or updating list items, to ensure a polished user experience!"
                    )
        ),

        )

    // obtaining an instance of Markwon
    val markwon = remember { Markwon.create(context) }

    val pagerState = rememberPagerState(pageCount = { lesson.size })
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(15.dp)),

        verticalArrangement = Arrangement.SpaceBetween
    ) {

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 15.dp, horizontal = 25.dp),
            verticalAlignment = Alignment.Top
        ) { page ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(),
            ) {
                items(1) {
                    lesson[page].forEach {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                it.value.forEach {
                                    Text(
                                        text = "${page + 1}. ${it.key}",
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 30.sp
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                    MarkdownText(markdown = it.value, markwon = markwon,darkTheme=darkTheme)
                                }
                            }
                            // The inner content should not be affected by verticalArrangement in parent Column
                        }
                    }
                }
            }
        }

        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier, markwon: Markwon,darkTheme:Boolean) {

    AndroidView(
        factory = { context ->
            TextView(context).apply {
                textSize = 18f // in SP
                if(darkTheme){
                    setTextColor(ContextCompat.getColor(context,R.color.white))
                }
                setLineSpacing(5.2f,1.3f)
                textAlignment = View.TEXT_ALIGNMENT_GRAVITY
            }
        },
        update = { textView ->
            markwon.setMarkdown(textView, markdown)
        },
        modifier = modifier.fillMaxWidth()
    )
}
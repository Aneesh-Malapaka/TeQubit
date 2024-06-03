package com.stormatte.tequbit

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.sharp.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import com.stormatte.tequbit.ui.theme.DarkBackground

@Composable
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
fun LessonChat() {
    val user = "Matte"
    val darkTheme = isSystemInDarkTheme()
    val demoMessages = remember {
        mutableStateListOf(

            MessageFormat("Input", SenderType.USER, "Hello, TeQubit!"),
            MessageFormat("Response", SenderType.AI, "Hello, $user"),
            MessageFormat(
                "Doubt",
                SenderType.USER,
                "Can you teach me about the basics of different mutable state- suffixes available in android studio? Like mutableStateListOf, mutableListOf and the MutableStateFlow"
            ),
            MessageFormat(
                "Response",
                SenderType.AI,
                "Sure, let me explain all these mutable state suffixes in detail. Before we start, let's think of these mutable states as **magical containers** in your Android app that hold data.  \n" +
                        "\n" +
                        "**Mutable State Suffixes Explained**\n" +
                        "\n" +
                        "1. **`mutableListOf<T>()`**: Think of this as a basic, **flexible box** you can use to store various things (data of type `T`) in a specific order. You can easily add, remove, and change items in this box. \n" +
                        "\n" +
                        "   ```kotlin\n" +
                        "   val myItems = mutableListOf<String>(\"apple\", \"banana\", \"cherry\") // Our box\n" +
                        "   myItems.add(\"grape\") // Adding more\n" +
                        "   myItems[1] = \"mango\" // Changing an item\n" +
                        "   myItems.removeAt(0) // Removing an item\n" +
                        "   ```\n" +
                        "\n" +
                        "2. **`mutableStateListOf<T>()`**: This is like a special **transparent box** that allows you to keep track of changes within it.  It's perfect for creating user interfaces where changes should automatically update the displayed information.  **Think of a shopping cart, where adding items to the cart updates the total automatically.**\n" +
                        "\n" +
                        "   ```kotlin\n" +
                        "   import androidx.compose.runtime.mutableStateListOf\n" +
                        "   import androidx.compose.runtime.remember\n" +
                        "\n" +
                        "   @Composable\n" +
                        "   fun MyComposable() {\n" +
                        "       val items = remember { mutableStateListOf(\"apple\", \"banana\", \"cherry\") } // Our transparent box\n" +
                        "\n" +
                        "       // Display the items, which will automatically update if items change\n" +
                        "       Column {\n" +
                        "           items.forEach { item -> \n" +
                        "               Text(text = item) \n" +
                        "           }\n" +
                        "       }\n" +
                        "\n" +
                        "       // Adding an item will update the display\n" +
                        "       Button(onClick = { items.add(\"grape\") }) {\n" +
                        "           Text(\"Add grape\")\n" +
                        "       }\n" +
                        "   }\n" +
                        "   ```\n" +
                        "\n" +
                        "3. **`MutableStateFlow<T>()`**: This is like a **magical stream** that delivers data to your app. Whenever something changes in this stream, it automatically notifies everyone listening. **Imagine a news channel that continuously broadcasts updates – that's `MutableStateFlow`!**\n" +
                        "\n" +
                        "   ```kotlin\n" +
                        "   import kotlinx.coroutines.flow.MutableStateFlow\n" +
                        "   import kotlinx.coroutines.flow.collect\n" +
                        "\n" +
                        "   val counter = MutableStateFlow(0) // Our magical stream starts at 0\n" +
                        "\n" +
                        "   // Listening to updates\n" +
                        "   launch {\n" +
                        "       counter.collect { value ->\n" +
                        "           println(\"Counter updated to: value\")\n" +
                        "       }\n" +
                        "   }\n" +
                        "\n" +
                        "   // Changing the value of the stream\n" +
                        "   counter.value = 5\n" +
                        "   ```\n" +
                        "\n" +
                        "**Key Takeaway**\n" +
                        "\n" +
                        "* **`mutableListOf`:** Simple, mutable list for storing data.\n" +
                        "* **`mutableStateListOf`:**  A mutable list designed specifically for reactive UI updates in Compose.\n" +
                        "* **`MutableStateFlow`:** A stream of data that allows you to efficiently manage and react to data changes.\n" +
                        "\n" +
                        "**Which one to use?**\n" +
                        "\n" +
                        "* **`mutableListOf`:** When you need a basic, mutable list without the need for reactive updates.\n" +
                        "* **`mutableStateListOf`:**  When working with Jetpack Compose and need a list that automatically updates UI elements when changes occur.\n" +
                        "* **`MutableStateFlow`:**  When you have data that needs to be shared across different parts of your app and requires efficient notification of changes.\n" +
                        "\n" +
                        "Let me know if you have any further questions! \uD83D\uDE0A \n"
            ),
            MessageFormat(
                "Lesson",
                SenderType.USER,
                "[LESSON] Okay, Can you explain it in detail?"
            ),
        )
    }

    Box(

    ) {
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
                    imageVector = Icons.Sharp.KeyboardArrowLeft,
                    contentDescription = "Back To Home",
                    modifier = Modifier
                        .clickable {

                        }
                        .width(35.dp)
                        .height(35.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = "New Chat")
                Spacer(modifier = Modifier.width(20.dp))

                Icon(
                    imageVector = Icons.Sharp.Settings,
                    contentDescription = "New Chat Icon",
                    modifier = Modifier
                        .clickable { }
                        .width(35.dp)
                        .height(35.dp)
                )

            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(demoMessages.size) { message ->
                    MessageDisplay(
                        index = message,
                        senderType = demoMessages[message].sender,
                        message = demoMessages[message].message
                    )
                }
            }
            //TextField Line
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
//                    .shadow(2.dp, ambientColor = Color(40000000))
                    .padding(top = 5.dp, bottom = 3.dp)
                    .width(320.dp)
                    .height(50.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = "Send Chat Icon",
                        modifier = Modifier
                            .conditional(darkTheme,
                                ifTrue = {background(Color(0x51D3CDCD), RoundedCornerShape(30.dp))},
                                ifFalse = {background(Color(0xE1000000), RoundedCornerShape(30.dp))}
                            )
                            .width(35.dp)
                            .height(35.dp),
                        tint = Color.White
                    )
                },
                shape = RoundedCornerShape(26.dp)
            )
        }
    }
}

@Composable
fun MessageDisplay(index: Int, senderType: Enum<SenderType>, message: String) {

    val applyMsgBackground: Boolean = senderType == SenderType.AI

    val color1 = randomColor()
    val color2 = randomColor()

    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .conditional(
                applyMsgBackground,
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
        horizontalArrangement = if (applyMsgBackground) Arrangement.Start else Arrangement.End
//        reverseLayout = senderType != SenderType.AI,

//        horizontalArrangement = Arr
    ) {

        if (applyMsgBackground) {
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
            Spacer(modifier = Modifier.width(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier
                        .width(250.dp)
//                        .wrapContentWidth()
                        .padding(start = 20.dp),
                    fontSize = 17.sp,
                    color = if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    textAlign = TextAlign.Justify
                )
            }
        } else {
            Row(
                modifier = Modifier
            ) {
                Text(
                    text = message,
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentWidth()
                        .padding(start = 20.dp),
                    fontSize = 17.sp,
                    color = if (isSystemInDarkTheme()) {
                        Color.White
                    } else {
                        Color.Black
                    },
                    textAlign = TextAlign.Justify
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "U",
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
        }
    }
}

data class MessageFormat(val type: String, val sender: Enum<SenderType>, val message: String)

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
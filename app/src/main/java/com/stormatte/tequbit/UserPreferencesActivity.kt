package com.stormatte.tequbit

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


val USER="Matt"

@Composable
fun UserPreference(navToHomePage: () -> Unit) {
    val context = LocalContext.current

    val preferenceSelected = remember {
        mutableStateListOf<PreferenceSelected>(
            PreferenceSelected(),
            PreferenceSelected(),
            PreferenceSelected()
        )
    }
    var knowledgePreference by remember {
        mutableStateOf("")
    }

    val selectedUsages = remember {
        mutableStateListOf<String>()
    }

    val selectedResponseWays = remember {
        mutableStateListOf<String>()
    }

    var isChecked by remember {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    val knowledgeData = listOf(
        KnowledgeLevel("Student", R.drawable.student),
        KnowledgeLevel("Entry Level", R.drawable.entry_level),
        KnowledgeLevel("Professional", R.drawable.professional),
        KnowledgeLevel("Self Learning", R.drawable.selflearnt),
    )

    val usageData = listOf(
        UsageInfo("Learning New Things", R.drawable.learningnewthings),
        UsageInfo("Interview Preparation", R.drawable.learningnewthings),
        UsageInfo("Doubts Clarification", R.drawable.doubts),
        UsageInfo("Casual Topics Clarification", R.drawable.casualsearch),
    )

    val respondingData = listOf(
        ResponseWayInfo("Witty & Fun Sentences", R.drawable.learningnewthings),
        ResponseWayInfo("Casual and Easy", R.drawable.learningnewthings),
        ResponseWayInfo("Include examples from games and pop culture", R.drawable.doubts),
        ResponseWayInfo("Let TeQubit decide based on question", R.drawable.casualsearch),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            text = "Select you level of knowledge - $knowledgePreference",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Left
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.height(550.dp)
        ) {
            items(knowledgeData.size) { index ->
                val knowledgeLevel = knowledgeData[index]
                SingleChoiceCard(
                    level = knowledgeLevel.level,
                    onCardSelected = {
                        knowledgePreference = it
                        if (preferenceSelected[0].selected) {
                            preferenceSelected[0].selected = false
                        } else {
                            preferenceSelected[0].selected = true
                            preferenceSelected[0].preferenceType = "Knowledge"
                        }
                    },
                    imageSrc = knowledgeLevel.image,
                    isSelected = knowledgeLevel.level == knowledgePreference
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(vertical = 30.dp, horizontal = 5.dp)
                .fillMaxWidth(),
            text = "Select your primary usage - ${selectedUsages.toList()}",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(570.dp)
        ) {
            items(usageData.size) { index ->
                val usageItem = usageData[index]
                MultipleChoiceCard(
                    usage = usageItem.usage,
                    onCardSelected = {
                        if (selectedUsages.contains(it)) {
                            selectedUsages.removeAll(listOf(it))
                        } else {
                            selectedUsages.add(it)

                        }

                        if (preferenceSelected[1].selected) {
                            preferenceSelected[1].selected = false
                        } else {
                            preferenceSelected[1].selected = true
                            preferenceSelected[1].preferenceType = "Usage"
                        }
                        println("The items are ${selectedUsages.toList()}")
                        isChecked = !isChecked
                    },
                    imageSrc = usageItem.image,
                    isSelected = isChecked
                )
            }
        }
        Text(
            modifier = Modifier.padding(vertical = 30.dp, horizontal = 5.dp),
            text = "Select how you want TeQubit to respond - ${selectedResponseWays.toList()}",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(600.dp)
        ) {
            items(respondingData.size) { index ->
                val resItem = respondingData[index]
                MultipleChoiceCard(
                    usage = resItem.wayToRespond,
                    onCardSelected = {
                        if (selectedResponseWays.contains(it)) {
                            selectedResponseWays.removeAll(listOf(it))
                        } else {
                            selectedResponseWays.add(it)
                        }

                        if (preferenceSelected[2].selected) {
                            preferenceSelected[2].selected = false
                            println("It shouldnt be here lmao")
                        } else {
                            preferenceSelected[2].selected = true
                            preferenceSelected[2].preferenceType = "Response Way"
                        }
                        println("The items are ${selectedResponseWays.toList()}")
                        isChecked = !isChecked
                    },
                    imageSrc = resItem.image,
                    isSelected = isChecked
                )
            }
        }

        ElevatedButton(
            modifier = Modifier.padding(20.dp),
            onClick = {

                if (knowledgePreference.isNotEmpty() && selectedUsages.isNotEmpty() && selectedResponseWays.isNotEmpty()) {
                    val userPreferences = UserPreferences(
                        knowledge = knowledgePreference,
                        usage = selectedUsages.toList(),
                        responseWay = selectedResponseWays.toList()
                    )
                    Firebase.database.getReference("users/$USER").setValue(userPreferences)
                    navToHomePage()
                } else {
                    Toast.makeText(
                        context,
                        "Pls select the preferences from all categories. U must have missed one or more.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ) {
            Text(
                text = "Submit Preferences",
                color = if (isSystemInDarkTheme()) {
                    Color.White
                } else {
                    Color.Black
                }
            )
        }
    }
}

@Composable
fun SingleChoiceCard(
    level: String,
    onCardSelected: (String) -> Unit,
    imageSrc: Int,
    isSelected: Boolean
) {

    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(
                onClick = {
                    onCardSelected(level)
                },
            )
            .padding(top = 10.dp, end = 8.dp),
        border = if (isSelected) BorderStroke(width = 4.dp, color = Color.Cyan) else BorderStroke(
            width = 1.dp,
            color = Color.Gray
        )
    ) {
        val image = painterResource(imageSrc)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                painter = image,
                contentScale = ContentScale.FillBounds,
                contentDescription = "$level knowledge"
            )

            Text(
                text = level,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (isSystemInDarkTheme()) {
                    Color.White
                } else {
                    Color.Black
                },
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

@Composable
fun MultipleChoiceCard(
    usage: String,
    onCardSelected: (String) -> Unit,
    imageSrc: Int,
    isSelected: Boolean
) {

    var isSelectedState by remember { mutableStateOf(isSelected) }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(
                onClick = {
                    onCardSelected(usage)
                    isSelectedState = !isSelectedState
                },
            )
            .padding(top = 10.dp, end = 8.dp),
        border = if (isSelectedState) BorderStroke(
            width = 4.dp,
            color = Color.Cyan
        ) else BorderStroke(
            width = 1.dp,
            color = Color.Gray
        )
    ) {
        val image = painterResource(imageSrc)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth(),
                painter = image,
                contentScale = ContentScale.FillBounds,
                contentDescription = usage
            )

            Text(
                text = usage,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = if (isSystemInDarkTheme()) {
                    Color.White
                } else {
                    Color.Black
                },
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}

data class UserPreferences(
    val knowledge: String,
    val usage: List<String>,
    val responseWay: List<String>,

)
data class KnowledgeLevel(val level: String, val image: Int)
data class UsageInfo(val usage: String, val image: Int)
data class ResponseWayInfo(val wayToRespond: String, val image: Int)

data class PreferenceSelected(var preferenceType: String = "", var selected: Boolean = false)
package com.stormatte.tequbit

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.stormatte.tequbit.ui.theme.TeQubitTheme
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel:QubitViewModel = viewModel()
            TeQubitTheme(
                darkTheme = viewModel.darkTheme.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QubitNavigation(navController = navController,viewModel)
                }
            }
        }
    }
}

suspend fun userExistsAndSelectedPreferences(): Pair<Boolean, Boolean>{
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Pair(false, false)
    val userPreferences = Firebase.database.getReference("users/$userId").get().await()
    return Pair(true, userPreferences.exists())
}


@SuppressLint("RestrictedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QubitNavigation(navController: NavHostController,viewModel: QubitViewModel){
    val chatViewModel :LessonChatWrapper = viewModel()
    NavHost(navController = navController, startDestination = "initialization" +
            "") {
        composable(route="user_login"){
            UserLogin(viewModel){
                navController.navigate("initialization"){
                    popUpTo(navController.graph.id)
                }
            }
        }
        composable("user_preference"){
            UserPreference({
                navController.navigate("initialization"){
                    popUpTo(navController.graph.id)
                }
            },viewModel)
        }
        composable("initialization"){
            LaunchedEffect(null){
                val (userExists, selectedPreferences) = userExistsAndSelectedPreferences()
                if(!userExists){
                    navController.navigate("user_login"){
                        popUpTo(navController.graph.id)
                    }
                }
                else if(!selectedPreferences){
                    navController.navigate("user_preference"){
                        popUpTo(navController.graph.id)
                    }
                }
                else{
                    navController.navigate("home_screen") {
                        popUpTo(navController.graph.id)
                    }
                }
            }
        }
        composable("home_screen"){
            println("The backstack is ${navController.previousBackStackEntry}")
            HomePage ({ destination, chatID ->
                run {
                    if (destination == "history") {
                        navController.navigate("learning_history")
                    }
                    else if(destination=="settings"){
                        navController.navigate("settings")
                    }
                    else{
                        navController.currentBackStackEntry?.savedStateHandle?.set("ChatID",chatID)
                        navController.navigate("new_chat")
                    }
                }
            },viewModel)
        }
        composable("learning_history"){
            LearningHistory({destination,chatId ->
                run{
                    if(destination=="settings"){
                        navController.navigate("settings")
                    } else if (destination=="home_screen") navController.navigate("home_screen")
                    else {
                        navController.currentBackStackEntry?.savedStateHandle?.set("ChatID", chatId)
                        navController.navigate("new_chat")
                    }
                }
            },viewModel)
        }
        composable("new_chat"){
            val chatIdToSend = navController.previousBackStackEntry?.savedStateHandle?.get<String>("ChatID")?:"failedID"
            LessonChat(
                chatViewModel,
                chatIdToSend,
                navController,
                viewModel)
        }
        composable("lesson_window"){
            val lesson = navController.previousBackStackEntry?.savedStateHandle?.get<List<Map<String, Map<String, String>>>>("lesson_data")?:listOf()
            LessonsWindow(lesson = lesson,viewModel = viewModel)

        }

        composable("settings"){
            SettingsScreen({ destination ->
                run {
                    when(destination) {
                        "updatePreferences" -> navController.navigate("user_preference")
                        "FAQ"-> navController.navigate("FAQ")
                    }
                }
            }, viewModel)
        }

        composable("FAQ"){
            FAQ_Screen(viewModel)
        }
    }
}

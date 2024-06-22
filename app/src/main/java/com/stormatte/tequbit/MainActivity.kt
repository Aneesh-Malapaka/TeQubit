package com.stormatte.tequbit

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stormatte.tequbit.ui.theme.TeQubitTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel:QubitViewModel = viewModel()

            TeQubitTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QubitNavigation(navController = navController)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QubitNavigation(navController: NavHostController){
    val chatViewModel :LessonChatWrapper = viewModel()
    NavHost(navController = navController, startDestination = "user_preference") {
        composable("user_preference"){
            UserPreference{
                navController.navigate("home_screen")
            }
        }
        composable("home_screen"){
            println("The backstack is ${it.arguments.toString()}")
            HomePage { destination, chatID ->
                run {
                    if (destination == "history") {
                        navController.navigate("learning_history")
                    }else{
                        navController.currentBackStackEntry?.savedStateHandle?.set("ChatID",chatID)
                        navController.navigate("new_chat")
                    }
                }
            }
        }
        composable("learning_history"){
//            navController.navigate("home_screen")
            LearningHistory{
                navController.navigate("new_chat")
            }
        }
        composable("new_chat"){
            val chatIdToSend = navController.previousBackStackEntry?.savedStateHandle?.get<String>("ChatID")?:"failedID"
            println(chatIdToSend)
            LessonChat(chatViewModel,chatIdToSend)
        }

    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TeQubitTheme {
        UserPreference({})
    }
}
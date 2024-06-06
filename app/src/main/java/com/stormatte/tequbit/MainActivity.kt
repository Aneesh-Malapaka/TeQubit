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

    NavHost(navController = navController, startDestination = "user_preference") {
        composable("user_preference"){
            UserPreference{
                navController.navigate("home_screen")
            }
        }
        composable("home_screen"){
            HomePage{
                if(it=="history")
                    navController.navigate("learning_history")
                else
                    navController.navigate("new_chat")
            }
        }
        composable("learning_history"){
//            navController.navigate("home_screen")
            LearningHistory{
                navController.navigate("new_chat")
            }
        }
        composable("new_chat"){
            LessonChat()
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
package com.stormatte.tequbit

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

enum class UIState{
    NOT_SIGNED_IN,
    SIGNED_IN,
    FAILED_SIGN_IN,
}
@SuppressLint("RestrictedApi")
@Composable
fun UserLogin(navToNext: ()->Unit){
    val uiState = remember{ mutableStateOf(UIState.NOT_SIGNED_IN) }
    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        if(res.resultCode == Activity.RESULT_OK){
            uiState.value = UIState.SIGNED_IN
            navToNext()
        }
    }
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.GitHubBuilder().build(),
        AuthUI.IdpConfig.AnonymousBuilder().build(),
    )
    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setLogo(R.drawable.ic_launcher_background)
        .setTheme(R.style.Theme_TeQubit)
        .setTosAndPrivacyPolicyUrls(
            "https://example.com/terms.html",
            "https://example.com/privacy.html"
        )
        .build()
    val context = LocalContext.current
    LaunchedEffect(uiState, context) {
        if(uiState.value == UIState.NOT_SIGNED_IN){
            signInLauncher.launch(signInIntent)
        }
        else if(uiState.value == UIState.NOT_SIGNED_IN){
            Toast.makeText(context, "You need to sign in to use the app", Toast.LENGTH_LONG).show()
        }

    }
}
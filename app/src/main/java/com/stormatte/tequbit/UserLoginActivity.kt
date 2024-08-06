package com.stormatte.tequbit

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

enum class UIState{
    NOT_SIGNED_IN,
    SIGNED_IN,
}
@SuppressLint("RestrictedApi")
@Composable
fun UserLogin(viewModel: QubitViewModel, navToNext: ()->Unit){
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
    )
    var theme = R.style.Theme_TeQubit_LIght
    if(viewModel.darkTheme.value)
        theme = R.style.Theme_TeQubit_Dark

    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setLogo(R.drawable.full_moon)
        .setTheme(theme)
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
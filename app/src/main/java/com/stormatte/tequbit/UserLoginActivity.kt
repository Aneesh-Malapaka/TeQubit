package com.stormatte.tequbit

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

enum class UIState {
    NOT_SIGNED_IN,
    SIGNED_IN,
}

@SuppressLint("RestrictedApi")
@Composable
fun UserLogin(viewModel: QubitViewModel, navToNext: () -> Unit) {
    val uiState = remember { mutableStateOf(UIState.NOT_SIGNED_IN) }
    val signInLauncher = rememberLauncherForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            uiState.value = UIState.SIGNED_IN
            navToNext()
        }
    }
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )
    var theme = R.style.Theme_TeQubit_Light
    if (viewModel.darkTheme.value)
        theme = R.style.Theme_TeQubit_Dark

    val signInIntent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .setLogo(R.drawable.tequbiticon)
        .setTheme(theme)
        .build()
    val context = LocalContext.current

    LaunchedEffect(uiState, context) {
        if (uiState.value == UIState.NOT_SIGNED_IN) {
            signInLauncher.launch(signInIntent)
        } else if (uiState.value == UIState.NOT_SIGNED_IN) {
            Toast.makeText(context, "You need to sign in to use the app", Toast.LENGTH_LONG).show()
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    val model: QubitViewModel = viewModel()
    UserLogin(viewModel = model) {

    }
}
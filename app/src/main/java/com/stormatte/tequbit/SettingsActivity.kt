package com.stormatte.tequbit

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.stormatte.tequbit.ui.theme.DarkSettingsBackground
import com.stormatte.tequbit.ui.theme.DarkSettingsHelpText
import com.stormatte.tequbit.ui.theme.LightSettingsBackground
import com.stormatte.tequbit.ui.theme.LightSettingsHelpText
import com.stormatte.tequbit.ui.theme.TeQubitTheme
//import com.stormatte.tequbit.ui.theme.faqIconColor
import com.stormatte.tequbit.ui.theme.faqIconColorDark
import com.stormatte.tequbit.ui.theme.faqIconColorLight
import com.stormatte.tequbit.ui.theme.feedbackIconColor
import com.stormatte.tequbit.ui.theme.updatePreferenceIconColorDark
import com.stormatte.tequbit.ui.theme.updatePreferenceIconColorLight

@Composable
fun SettingsScreen(navFromSettings: (destinationName:String) -> Unit, viewModel: QubitViewModel) {
    val displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: "User"
    val userImage = FirebaseAuth.getInstance().currentUser?.photoUrl
    val painter = rememberAsyncImagePainter(userImage)
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxHeight()
    ) {
        // Account Info
        Column(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .conditional(
                        viewModel.darkTheme.value,
                        ifTrue = { background(DarkSettingsBackground) },
                        ifFalse = {
                            background(
                                LightSettingsBackground,
                            )
                        }
                    )
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .fillMaxWidth()
                ,
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                AsyncImage(
//                    model = ,
//                    contentDescription = ,
//                    imageLoader = )
                Image(
                    painter = painter,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(50)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier.clickable {
                        navFromSettings("userDetails")
                    }
                ) {
                    Text(
                        text = displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "View your details here",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if(viewModel.darkTheme.value){
                            DarkSettingsHelpText
                        }else{
                            LightSettingsHelpText
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        //General Settings
        Column(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "General",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .conditional(
                        viewModel.darkTheme.value,
                        ifTrue = { background(DarkSettingsBackground) },
                        ifFalse = {
                            background(
                                LightSettingsBackground,
                            )
                        }
                    )
                    .padding(vertical = 20.dp, horizontal = 15.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Update Preferences",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .clip(RoundedCornerShape(50)),
                        tint = if(viewModel.darkTheme.value) updatePreferenceIconColorDark else updatePreferenceIconColorLight
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.clickable {
                            navFromSettings("updatePreferences")
                        }
                    ) {
                        Text(
                            text = "Update Preferences",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Update your chat preferences here",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if(viewModel.darkTheme.value){
                                DarkSettingsHelpText
                            }else{
                                LightSettingsHelpText
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "F.A.Q",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .clip(RoundedCornerShape(50)),
                        tint = if (viewModel.darkTheme.value ) faqIconColorDark else faqIconColorLight
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        modifier = Modifier.clickable {
                            navFromSettings("FAQ")
                        }
                    ) {
                        Text(
                            text = "Frequently Asked Questions",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "FAQ on using chatbot efficiently",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if(viewModel.darkTheme.value){
                                DarkSettingsHelpText
                            }else{
                                LightSettingsHelpText
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Feedback",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .clip(RoundedCornerShape(50)),
                        tint = feedbackIconColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Leave a Feedback",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Have a feedback for the app? Tell us here",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if(viewModel.darkTheme.value){
                                DarkSettingsHelpText
                            }else{
                                LightSettingsHelpText
                            }
                        )
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "Theme",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .conditional(
                        viewModel.darkTheme.value,
                        ifTrue = { background(DarkSettingsBackground) },
                        ifFalse = {
                            background(
                                LightSettingsBackground,
                            )
                        }
                    )
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if(viewModel.darkTheme.value) "Change To Light Mode" else "Change To Dark Mode",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )

                Switch(
                    checked = !viewModel.darkTheme.value,
                    onCheckedChange = {
                        Log.d("ThemeSwitch", "The current value is ${viewModel.darkTheme.value} ")
                        viewModel.setDarkThemeValue(!it)
                        Log.d("ThemeSwitch", "Changed to value is ${viewModel.darkTheme.value} ")
                    },
                    thumbContent = {
                        Icon(
                            painter = painterResource(
                                id = if (viewModel.darkTheme.value) R.drawable.full_moon else R.drawable.sun
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                            tint = Color.Unspecified
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF504306),
                        checkedTrackColor = MaterialTheme.colorScheme.background,
                        uncheckedThumbColor = Color.Black,
                        uncheckedTrackColor = MaterialTheme.colorScheme.background,
                    ),
                )
            }
        }
    }
}
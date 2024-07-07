package com.stormatte.tequbit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stormatte.tequbit.ui.theme.DarkIconsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(navToNextScreen:(destinationName:String,chatID:String)->Unit){
  val model :QubitViewModel = viewModel()
  val caro_images = listOf(R.drawable.carousel_slide_1,R.drawable.entry_level,R.drawable.learningnewthings)
  val pagerState = rememberPagerState(pageCount= { caro_images.size })
  val pagerSize = remember {
    mutableStateListOf(0,0)
  }
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopStart
  ){
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(30.dp),
      verticalArrangement = Arrangement.SpaceAround,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {

      Text(
        text = "Hello. I am TeQubit. How can I help you today?",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 40.sp
      )

      //Column Layout for Buttons
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        ElevatedButton(
          modifier = Modifier
            .height(70.dp)
            .width(270.dp),
          shape = RoundedCornerShape(20.dp),
          onClick = {
              val chatID = generateChatID()
              navToNextScreen("new_chat", chatID)
          }
        ) {
          Text(
            text = "Start a New Learning Session",
            color = if(isSystemInDarkTheme()){
              Color.White
            }else{
              Color.Black
            }
          )
          Spacer(modifier = Modifier.width(10.dp))
          Icon(imageVector = Icons.Sharp.Add, contentDescription = "New Chat Icon")
        }
        Spacer(modifier = Modifier.height(30.dp))
        ElevatedButton(
          modifier = Modifier
            .height(70.dp)
            .width(270.dp),
          shape = RoundedCornerShape(20.dp),
          onClick = {
            navToNextScreen("history","")
          }
        ) {
          Text(
            text = "Your Learning History",
            color = if(isSystemInDarkTheme()){
                Color.White
            }else{
              Color.Black
            }
          )
          Spacer(modifier = Modifier.width(55.dp))
          Icon(
            imageVector = Icons.Sharp.Refresh,
            contentDescription = "Learnings History Icon",
          )
        }

      }
      
      HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(10.dp),
        pageSize = PageSize.Fill,
        modifier = Modifier
          .fillMaxHeight(0.5f)
         ){
        page ->
          Image(
            modifier = Modifier
//              .width(1000.dp)
//              .height(726.dp)
            ,
            painter = painterResource(id = caro_images[page] ),
            contentScale = ContentScale.Inside,
            contentDescription = "Carousel Slide 1"
          )

      }

    }
  }
}
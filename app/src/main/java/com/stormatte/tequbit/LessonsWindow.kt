package com.stormatte.tequbit

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import io.noties.markwon.Markwon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LessonsWindow(lesson: List<Map<String, Map<String, String>>>, viewModel: QubitViewModel) {
    val context = LocalContext.current

    val darkTheme = viewModel.darkTheme.value

    // obtaining an instance of Markwon
    val markwon = remember { Markwon.create(context) }

    val pagerState = rememberPagerState(pageCount = { lesson.size })
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .border(2.dp, Color.Gray, shape = RoundedCornerShape(15.dp)),

        verticalArrangement = Arrangement.SpaceBetween
    ) {

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 15.dp, horizontal = 25.dp),
            verticalAlignment = Alignment.Top
        ) { page ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(),
            ) {
                items(1) {
                    lesson[page].forEach {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                it.value.forEach {
                                    Text(
                                        text = "${page + 1}. ${it.key}",
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 30.sp
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                    MarkdownText(
                                        markdown = it.value,
                                        markwon = markwon,
                                        darkTheme = darkTheme
                                    )
                                }
                            }
                            // The inner content should not be affected by verticalArrangement in parent Column
                        }
                    }
                }
            }
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

@Composable
fun MarkdownText(markdown: String, modifier: Modifier = Modifier, markwon: Markwon,darkTheme:Boolean) {

    AndroidView(
        factory = { context ->
            TextView(context).apply {
                textSize = 18f // in SP
                if(darkTheme){
                    setTextColor(ContextCompat.getColor(context,R.color.white))
                }
                setLineSpacing(5.2f,1.3f)
                textAlignment = View.TEXT_ALIGNMENT_GRAVITY
            }
        },
        update = { textView ->
            markwon.setMarkdown(textView, markdown)
        },
        modifier = modifier.fillMaxWidth()
    )
}
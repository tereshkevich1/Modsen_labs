@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalFoundationApi::class,
    ExperimentalFoundationApi::class
)

package com.example.onboarding.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OnBoardScreen() {
    val pagesList = listOf(
        OnBoardingPage.OnBoarding1,
        OnBoardingPage.OnBoarding2,
        OnBoardingPage.OnBoarding3,
        OnBoardingPage.OnBoarding4
    )

    val pagerState = rememberPagerState(pageCount = { pagesList.size })

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { position ->
            PagerScreen(pagesList[position])
        }

        // HorizontalPagerIndicator(pagerState)
    }
}

@Composable
fun HorizontalPagerIndicator(pagerState: PagerState) {
    val rowModifier = Modifier
        .wrapContentHeight()
        .fillMaxWidth()
        .padding(bottom = 8.dp)

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color =
                if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray

            val shape =
                if (pagerState.currentPage == iteration) RoundedCornerShape(8.dp) else CircleShape

            val width =
                if (pagerState.currentPage == iteration) 24.dp else 12.dp

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(shape)
                    .background(color)
                    .height(12.dp)
                    .width(width)
            )
        }
    }

}

@Composable
@Preview
fun OnBoardScreenPreview() {
    OnBoardScreen()
}
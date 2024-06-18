package com.example.onboarding.main

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.onboarding.R

sealed class OnBoardingPage(
    @StringRes
    val title: Int,
    @StringRes
    val text: Int,
    @DrawableRes
    val image: Int,
    @ColorRes
    val backgroundColor: Int
) {
    data object OnBoarding1 : OnBoardingPage(
        title = R.string.title_onboarding_1,
        text = R.string.text_onboarding_1,
        image = R.drawable.img_car1,
        backgroundColor = R.color.onboarding_1_background_color
    )

    data object OnBoarding2 : OnBoardingPage(
        title = R.string.title_onboarding_2,
        text = R.string.text_onboarding_2,
        image = R.drawable.img_car2,
        backgroundColor = R.color.onboarding_2_background_color
    )

    data object OnBoarding3 : OnBoardingPage(
        title = R.string.title_onboarding_3,
        text = R.string.text_onboarding_3,
        image = R.drawable.img_car3,
        backgroundColor = R.color.onboarding_3_background_color
    )

    data object OnBoarding4 : OnBoardingPage(
        title = R.string.title_onboarding_4,
        text = R.string.text_onboarding_4,
        image = R.drawable.img_car4,
        backgroundColor = R.color.onboarding_4_background_color
    )
}
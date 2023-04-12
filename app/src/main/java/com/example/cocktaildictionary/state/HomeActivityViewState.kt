package com.example.cocktaildictionary.state

import com.example.cocktaildictionary.network.CocktailList

/*
*This is an implementation of [State] that describes the configuration of the loading screen at a given time
 */

data class HomeActivityViewState(
    val showHeading : Boolean = true,
    val showProgressCircle: Boolean = true,
    val data: CocktailList? = null,
    val error: Throwable? = null,
    val isAppBeingRefreshed: Boolean? = false
):State
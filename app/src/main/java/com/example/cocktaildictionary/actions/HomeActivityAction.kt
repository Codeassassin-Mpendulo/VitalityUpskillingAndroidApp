package com.example.cocktaildictionary.actions

import com.example.cocktaildictionary.network.CocktailList

/*
*These are all of the possible actions that can be triggered from the Loading screen
*/

sealed class HomeActivityAction: Action {
    object LoadingStarted : HomeActivityAction()
    data class LoadingSuccess(val newData: CocktailList?) : HomeActivityAction()
    data class LoadingFailure(val error: Throwable?) : HomeActivityAction()
    data class LoadFilteredList(val filteredList:CocktailList?):HomeActivityAction()
    object RefreshApp :HomeActivityAction()
}
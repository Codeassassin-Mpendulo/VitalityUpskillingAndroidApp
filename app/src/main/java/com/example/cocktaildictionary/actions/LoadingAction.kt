package com.example.cocktaildictionary.actions

import com.example.cocktaildictionary.network.CocktailList

/*
*These are all of the possible actions that can be triggered from the Loading screen
*/

sealed class LoadingAction: Action {
    object LoadingStarted : LoadingAction()
    data class LoadingSuccess(val newData: CocktailList?) : LoadingAction()
    data class LoadingFailure(val error: Throwable?) : LoadingAction()
    data class LoadFilteredList(val filteredList:CocktailList?):LoadingAction()
    object RefreshApp :LoadingAction()
}
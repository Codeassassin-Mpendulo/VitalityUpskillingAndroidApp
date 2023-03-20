package com.example.task1.actions


import com.example.task1.network.CocktailList


/*
*These are all of the possible actions that can be triggered from the Loading screen
*/

sealed class LoadingAction: Action {



    object LoadingStarted : LoadingAction()
    data class LoadingSuccess(val newData: CocktailList?) : LoadingAction()
    data class LoadingFailure(val error: Throwable?) : LoadingAction()
}
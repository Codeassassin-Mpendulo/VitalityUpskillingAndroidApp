package com.example.cocktaildictionary.state

import com.example.cocktaildictionary.network.CocktailList

sealed class HomeActivityStates:State{

    object Loading : HomeActivityStates()
    data class DataLoadSuccessful(var cocktailList : CocktailList) : HomeActivityStates()
    data class DataLoadFail(var error : Throwable) : HomeActivityStates()
}
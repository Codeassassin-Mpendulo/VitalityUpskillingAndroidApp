package com.example.cocktaildictionary.reducer


import com.example.cocktaildictionary.actions.HomeActivityAction
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.state.HomeActivityStates

class HomeActivityReducer : Reducer<HomeActivityAction,HomeActivityStates>{
    override fun reduce(action: HomeActivityAction): HomeActivityStates {
        return when(action){
            is HomeActivityAction.LoadingStarted -> {
                HomeActivityStates.Loading
            }
            is HomeActivityAction.LoadingSuccess ->{
                HomeActivityStates.DataLoadSuccessful(action.newData)
            }
            is HomeActivityAction.LoadingFailure ->{
                HomeActivityStates.DataLoadFail(action.error)
            }
        }
    }
}
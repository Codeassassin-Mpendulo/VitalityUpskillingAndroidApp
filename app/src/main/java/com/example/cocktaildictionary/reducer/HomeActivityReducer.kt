package com.example.cocktaildictionary.reducer


import com.example.cocktaildictionary.actions.HomeActivityAction
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.state.HomeActivityStates

class HomeActivityReducer : Reducer<HomeActivityStates,HomeActivityAction>{
    override fun reduce(currentState: HomeActivityStates, action: HomeActivityAction): HomeActivityStates {
        return when(action){
            is HomeActivityAction.LoadingStarted -> {
                HomeActivityStates.Loading
            }
            is HomeActivityAction.LoadingSuccess ->{
                HomeActivityStates.DataLoadSuccessful(action.newData?: CocktailList(Cocktails = emptyList()))
            }
            is HomeActivityAction.LoadingFailure ->{
                HomeActivityStates.DataLoadFail(action.error?:Throwable("Background Error"))
            }
            is HomeActivityAction.LoadFilteredList ->{
                HomeActivityStates.DataFilteredSuccessful(action.filteredList?: CocktailList(Cocktails = emptyList()))
            }
            is HomeActivityAction.RefreshApp ->{
                HomeActivityStates.RefreshApp
            }
        }
    }


}
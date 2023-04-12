package com.example.cocktaildictionary.reducer


import com.example.cocktaildictionary.actions.LoadingAction
import com.example.cocktaildictionary.state.HomeActivityViewState

class LoadingReducer : Reducer<HomeActivityViewState,LoadingAction>{
    override fun reduce(currentState: HomeActivityViewState, action: LoadingAction): HomeActivityViewState {
        return when(action){
            is LoadingAction.LoadingStarted -> {
                stateMappedToLoadingStartedAction(currentState)
            }
            is LoadingAction.LoadingSuccess ->{
                stateMappedToLoadingSuccessAction(currentState, action)
            }
            is LoadingAction.LoadingFailure ->{
                stateMappedToLoadingFailureAction(currentState, action)
            }
            is LoadingAction.LoadFilteredList ->{
                currentState.copy(
                    data = action.filteredList
                )
            }
            is LoadingAction.RefreshApp ->{
                currentState.copy(
                    isAppBeingRefreshed = !currentState.isAppBeingRefreshed!!
                )
            }
        }
    }

    private fun stateMappedToLoadingFailureAction(
        currentState: HomeActivityViewState,
        action: LoadingAction.LoadingFailure
    ) = currentState.copy(
        showHeading = false,
        showProgressCircle = false,
        error = action.error,
        data = null,
        isAppBeingRefreshed = false
    )

    private fun stateMappedToLoadingSuccessAction(
        currentState: HomeActivityViewState,
        action: LoadingAction.LoadingSuccess
    ) = currentState.copy(
        showHeading = false,
        showProgressCircle = false,
        data = action.newData,
        error = null,
        isAppBeingRefreshed = false
    )


    private fun stateMappedToLoadingStartedAction(currentState: HomeActivityViewState) =
        currentState.copy(
            showHeading = true,
            showProgressCircle = true,
            data = null,
            error = null,
            isAppBeingRefreshed = false
        )
}
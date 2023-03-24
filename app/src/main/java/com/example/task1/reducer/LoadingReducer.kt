package com.example.task1.reducer


import com.example.task1.actions.LoadingAction
import com.example.task1.state.LoadingViewState


class LoadingReducer : Reducer<LoadingViewState,LoadingAction>{
    override fun reduce(currentState: LoadingViewState, action: LoadingAction): LoadingViewState {

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
        currentState: LoadingViewState,
        action: LoadingAction.LoadingFailure
    ) = currentState.copy(
        showHeading = false,
        showProgressCircle = false,
        error = action.error
    )

    private fun stateMappedToLoadingSuccessAction(
        currentState: LoadingViewState,
        action: LoadingAction.LoadingSuccess
    ) = currentState.copy(
        showHeading = false,
        showProgressCircle = false,
        data = action.newData
    )


    private fun stateMappedToLoadingStartedAction(currentState: LoadingViewState) =
        currentState.copy(
            showHeading = true,
            showProgressCircle = true,
        )
}
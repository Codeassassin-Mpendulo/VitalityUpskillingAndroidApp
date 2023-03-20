package com.example.task1.store

import com.example.task1.actions.Action
import com.example.task1.reducer.Reducer
import com.example.task1.state.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
*A store is out state container for a given screen
* @param[initialState] is the initial state of the screen when it is first created
* @param[reducer] is a system for taking the current state and a new action, and
* outputting the the update state
 */


class Store<S:State,A:Action>(
    initialState: S,
    private val reducer: Reducer<S, A>
) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    fun dispatch(action: A){
        val currentState = _state.value
        val newState = reducer.reduce(currentState,action)
        _state.value = newState
    }

}
package com.example.cocktaildictionary.store

import com.example.cocktaildictionary.actions.Action
import com.example.cocktaildictionary.reducer.Reducer
import com.example.cocktaildictionary.state.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
*A store is out state container for a given screen
* @param[initialState] is the initial state of the screen when it is first created
* @param[reducer] is a system for taking the current state and a new action, and
* outputting the the update state
 */

class Store<A : Action, S : State>(
    initialState: S,
    private val reducer: Reducer<A, S>
) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    fun dispatch(action: A) {
        val newState = reducer.reduce(action)
        _state.value = newState
    }
}
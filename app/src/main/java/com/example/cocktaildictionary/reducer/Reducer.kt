package com.example.cocktaildictionary.reducer

import com.example.cocktaildictionary.actions.Action
import com.example.cocktaildictionary.state.State

interface Reducer<S:State,A:Action> {
    fun reduce(currentState: S, action: A): S
}
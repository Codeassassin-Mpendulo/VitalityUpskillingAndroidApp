package com.example.task1.reducer

import com.example.task1.actions.Action
import com.example.task1.state.State

interface Reducer<S:State,A:Action> {
    fun reduce(currentState: S, action: A): S
}
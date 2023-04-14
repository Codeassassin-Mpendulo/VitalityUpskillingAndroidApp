package com.example.cocktaildictionary.reducer

import com.example.cocktaildictionary.actions.Action
import com.example.cocktaildictionary.state.State

interface Reducer<A:Action,S:State> {
    fun reduce(action: A): S
}
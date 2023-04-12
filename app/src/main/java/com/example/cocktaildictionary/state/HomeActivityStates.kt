package com.example.cocktaildictionary.state

import com.example.cocktaildictionary.network.CocktailList

sealed class HomeActivityStates{

    class Loading(
        private val showHeading: Boolean = true,
        private val showProgressCircle: Boolean = true,
        val data: CocktailList? = null,
        private val error: Throwable? = null,
        private val isAppBeingRefreshed: Boolean? = false
    ) : State{
        fun response():HomeActivityViewState {
            return HomeActivityViewState(
                showHeading = this.showHeading,
                showProgressCircle = this.showProgressCircle,
                data = this.data,
                error = this.error,
                isAppBeingRefreshed = this.isAppBeingRefreshed
            )
        }
    }

    class DataLoadSuccessful(
        private val showHeading : Boolean = false,
        private val showProgressCircle: Boolean = false,
        private val error: Throwable? = null,
        private val isAppBeingRefreshed: Boolean? = false
    ):State{
        fun response(data: CocktailList?):HomeActivityViewState {
            return HomeActivityViewState(
                showHeading = this.showHeading,
                showProgressCircle = this.showProgressCircle,
                data = data,
                error = this.error,
                isAppBeingRefreshed = this.isAppBeingRefreshed
            )
        }
    }

    class DataLoadFail(
        private val showHeading : Boolean = false,
        private val showProgressCircle: Boolean = false,
        val data: CocktailList? = null,
        private val isAppBeingRefreshed: Boolean? = false
    ):State{
        fun response(error: Throwable?):HomeActivityViewState {
            return HomeActivityViewState(
                showHeading = this.showHeading,
                showProgressCircle = this.showProgressCircle,
                data = this.data,
                error = error,
                isAppBeingRefreshed = this.isAppBeingRefreshed
            )
        }
    }

    class AppRefresh(
        private val showHeading : Boolean = false,
        private val showProgressCircle: Boolean = false,
        private val data: CocktailList? = null,
        private val error: Throwable? = null,
    ):State{
        fun response(boolean: Boolean):HomeActivityViewState {
            return HomeActivityViewState(
                showHeading = this.showHeading,
                showProgressCircle = this.showProgressCircle,
                data = this.data,
                error = this.error,
                isAppBeingRefreshed = boolean
            )
        }
    }
}
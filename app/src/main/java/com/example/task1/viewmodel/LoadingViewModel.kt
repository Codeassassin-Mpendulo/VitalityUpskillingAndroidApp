package com.example.task1.viewmodel

import androidx.lifecycle.ViewModel
import com.example.task1.actions.LoadingAction
import com.example.task1.network.CocktailApiServices
import com.example.task1.network.CocktailList
import com.example.task1.network.RetrofitClientInstance
import com.example.task1.reducer.LoadingReducer
import com.example.task1.state.LoadingViewState
import com.example.task1.store.Store
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.StateFlow

class LoadingViewModel: ViewModel() {


   private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()

   private val store = Store(
      initialState = LoadingViewState(),
      reducer = LoadingReducer()
   )

   val viewState: StateFlow<LoadingViewState> = store.state

   private fun startLoading(){
      val action = LoadingAction.LoadingStarted
      store.dispatch(action)
   }

   private fun successfulLoad(cocktailList: CocktailList){
      val action = LoadingAction.LoadingSuccess(cocktailList)
      store.dispatch(action)
   }

   private fun failedLoad(error:Throwable?){
      val action = LoadingAction.LoadingFailure(error)
      store.dispatch(action)
   }


   fun getPopularCocktails() {
      startLoading()
      val service = RetrofitClientInstance.retrofitInstance?.create(CocktailApiServices::class.java)

      myCompositeDisposable.add(service!!.getCocktails()
         .observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.io())
         .subscribe(
            { res ->
               successfulLoad(res)
            },
            { throwable ->
               failedLoad(throwable)
            }
         )
      )

   }



}
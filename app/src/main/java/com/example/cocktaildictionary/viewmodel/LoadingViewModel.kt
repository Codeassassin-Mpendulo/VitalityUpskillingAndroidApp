package com.example.cocktaildictionary.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.cocktaildictionary.actions.LoadingAction
import com.example.cocktaildictionary.cache.Cache
import com.example.cocktaildictionary.cache.CacheDatabase
import com.example.cocktaildictionary.network.Cocktail
import com.example.cocktaildictionary.network.CocktailApiServices
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.network.RetrofitClientInstance
import com.example.cocktaildictionary.reducer.LoadingReducer
import com.example.cocktaildictionary.state.HomeActivityViewState
import com.example.cocktaildictionary.store.Store
import com.example.cocktaildictionary.utils.Converters
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class LoadingViewModel(application: Application): AndroidViewModel(application) {

   private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()
   private var cacheDatabase: CacheDatabase
   private val converter:Converters = Converters()
   private var cocktailList: CocktailList? = null
   private val store = Store(
      initialState = HomeActivityViewState(),
      reducer = LoadingReducer()
   )
   val viewState: StateFlow<HomeActivityViewState> = store.state

   init {
      cacheDatabase = CacheDatabase.getDatabase(application)
   }

   fun isCocktailListNull():Boolean{
      if (this.cocktailList != null){
         return false
      }
      return true
   }

   private fun insertIntoCache(time: String, cocktailList: CocktailList) {
      val cache = Cache(null,time,converter.fromCocktailList(cocktailList.Cocktails))
      GlobalScope.launch(Dispatchers.IO) {
         cacheDatabase.cacheDao().addToCache(cache)
      }
      Log.d(TAG,"Successfully added to cache")
   }

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

   fun loadFilteredData(query: String?){
      var tempCocktailList = mutableListOf<Cocktail>()
      val action : LoadingAction = if (query != null) {
         for (cocktail in cocktailList!!.Cocktails) {
              if (cocktail.drinkName.contains(query) || cocktail.drinkInstruction.contains(query)){
                 tempCocktailList.add(cocktail)
              }
         }
         LoadingAction.LoadFilteredList(CocktailList(tempCocktailList))
      } else{
         LoadingAction.LoadFilteredList(cocktailList)
      }
      store.dispatch(action)
   }

   fun getPopularCocktails() {
      startLoading()
      GlobalScope.launch(Dispatchers.IO) {
         if (cacheDatabase.cacheDao().mostRecentData().isNotEmpty()){
            //if Cache entity is not null, get the List of cocktails from an instance of the Cache Entity
            val mostRecentData = converter.fromString(cacheDatabase.cacheDao().mostRecentData()[0].latestData)

            if(mostRecentData != null){
               //if the List of cocktails is not null, then convert it into a CockTailList and pass it to the successfulLoad method
               val cocktailList = CocktailList(mostRecentData)
               successfulLoad(cocktailList)
               return@launch
            }
         }
      }

      val service = RetrofitClientInstance.retrofitInstance?.create(CocktailApiServices::class.java)
      myCompositeDisposable.add(service!!.getCocktails()
         .observeOn(AndroidSchedulers.mainThread())
         .subscribeOn(Schedulers.io())
         .subscribe(
            { res ->
               successfulLoad(res)
               insertIntoCache(LocalDateTime.now().toString(),res)
               this.cocktailList = res
            },
            { throwable ->
               failedLoad(throwable)
            }
         )
      )
   }
}
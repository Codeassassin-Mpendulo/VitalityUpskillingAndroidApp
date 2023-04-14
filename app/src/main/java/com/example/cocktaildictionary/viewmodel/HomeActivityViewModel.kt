package com.example.cocktaildictionary.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.cocktaildictionary.actions.HomeActivityAction
import com.example.cocktaildictionary.cache.Cache
import com.example.cocktaildictionary.cache.CacheDatabase
import com.example.cocktaildictionary.network.Cocktail
import com.example.cocktaildictionary.network.CocktailApiServices
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.network.RetrofitClientInstance
import com.example.cocktaildictionary.reducer.HomeActivityReducer
import com.example.cocktaildictionary.state.HomeActivityStates
import com.example.cocktaildictionary.store.Store
import com.example.cocktaildictionary.utils.Converters
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeActivityViewModel(application: Application): AndroidViewModel(application) {

   private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()
   private var cacheDatabase: CacheDatabase
   private val converter:Converters = Converters()

   private val store = Store(
      initialState = HomeActivityStates.Loading,
      reducer = HomeActivityReducer()
   )
   val viewState: StateFlow<HomeActivityStates> = store.state

   init {
      cacheDatabase = CacheDatabase.getDatabase(application)
   }

   private fun insertIntoCache(time: String, cocktailList: CocktailList) {
      val cache = Cache(null,time,converter.fromCocktailList(cocktailList.Cocktails))
      GlobalScope.launch(Dispatchers.IO) {
         cacheDatabase.cacheDao().addToCache(cache)
      }
      Log.d(TAG,"Successfully added to cache")
   }

   private fun startLoading(){
      val action = HomeActivityAction.LoadingStarted
      store.dispatch(action)
   }

   private fun successfulLoad(cocktailList: CocktailList){
      val action = HomeActivityAction.LoadingSuccess(cocktailList)
      store.dispatch(action)
   }

   private fun failedLoad(error:Throwable){
      val action = HomeActivityAction.LoadingFailure(error)
      store.dispatch(action)
   }

   fun getPopularCocktails() {
      GlobalScope.launch(Dispatchers.IO) {
         if (cacheDatabase.cacheDao().mostRecentData().isNotEmpty()){
            //if Cache entity is not null, get the List of cocktails from an instance of the Cache Entity
            val mostRecentData = converter.fromString(cacheDatabase.cacheDao().mostRecentData()[0].latestData)

            if(mostRecentData != null){
               //if the List of cocktails is not null, then convert it into a CockTailList and pass it to the successfulLoad method
               val cocktailList = CocktailList(mostRecentData)
               successfulLoad(cocktailList)
            }
            return@launch
         }
         else{
            val service = RetrofitClientInstance.retrofitInstance?.create(CocktailApiServices::class.java)
            myCompositeDisposable.add((service?.getCocktails()?: Observable.just(CocktailList(emptyList())))
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeOn(Schedulers.io())
               .subscribe(
                  { res ->
                     successfulLoad(res)
                     insertIntoCache(LocalDateTime.now().toString(),res)
                  },
                  { throwable ->
                     failedLoad(throwable)
                  }
               )
            )
         }
      }
      return
   }
}

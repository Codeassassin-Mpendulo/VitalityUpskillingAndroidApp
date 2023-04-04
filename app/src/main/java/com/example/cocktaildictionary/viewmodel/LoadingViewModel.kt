package com.example.cocktaildictionary.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import android.view.Menu
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cocktaildictionary.actions.LoadingAction
import com.example.cocktaildictionary.cache.Cache
import com.example.cocktaildictionary.cache.CacheDatabase
import com.example.cocktaildictionary.network.Cocktail
import com.example.cocktaildictionary.network.CocktailApiServices
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.network.RetrofitClientInstance
import com.example.cocktaildictionary.reducer.LoadingReducer
import com.example.cocktaildictionary.state.LoadingViewState
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

   companion object{
      private const val DONE = 0L
      private const val ONE_SECOND = 1000L
      private const val COUNTDOWN_TIME = 60000L
   }


   private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()
   private val timer : CountDownTimer
   private var cacheDatabase: CacheDatabase
   private val converter:Converters = Converters()
   private var cocktailList: CocktailList? = null
   private var hasTimerStarted: Boolean = false
   private val _time = MutableLiveData<Long>()
   private val _hasTimerStopped = MutableLiveData<Boolean>()
   private val hasTimerStopped : LiveData<Boolean>
      get() = _hasTimerStopped


   init {
      timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
         override fun onTick(millisUntilFinished: Long) {

            _time.value = (millisUntilFinished/ ONE_SECOND)
            _hasTimerStopped.value = false
            hasTimerStarted = true
            Log.d("Time:", DateUtils.formatElapsedTime(_time.value!!))

         }

         override fun onFinish() {
            _time.value = DONE
            _hasTimerStopped.value = true
            hasTimerStarted = false
            this.cancel()
         }
      }




      cacheDatabase = CacheDatabase.getDatabase(application)

      this.hasTimerStopped.observeForever { hasFinished ->
         if (hasFinished) {
            GlobalScope.launch(Dispatchers.IO) {
               cacheDatabase.cacheDao().deleteAll()
            }
         }
      }

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

   private fun appRefresh(){
      val action = LoadingAction.RefreshApp
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

            if(!hasTimerStarted){
               timer.start()
            }


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

   fun refreshApp(swipeRefreshLayout: SwipeRefreshLayout, menu: Menu) {
      appRefresh()
      this.getPopularCocktails()
      menu.close()
      swipeRefreshLayout.isRefreshing = false
      appRefresh()
   }


}
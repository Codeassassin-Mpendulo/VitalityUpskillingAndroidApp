package com.example.cocktaildictionary.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

   companion object{
      private const val DONE = 0L
      private const val ONE_SECOND = 1000L
      private const val COUNTDOWN_TIME = 60000L
   }
   private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()
   private val timer : CountDownTimer
   private var cacheDatabase: CacheDatabase
   private val converter:Converters = Converters()
   private lateinit var cocktailList: CocktailList
   private val store = Store(
      initialState = HomeActivityStates.Loading,
      reducer = HomeActivityReducer()
   )
   val viewState: StateFlow<HomeActivityStates> = store.state
   private val _time = MutableLiveData<Long>()
   private val _hasTimerStopped = MutableLiveData<Boolean>()
   private val hasTimerStopped : LiveData<Boolean>
      get() = _hasTimerStopped

   init {
      _hasTimerStopped.value = true
      cacheDatabase = CacheDatabase.getDatabase(application)
      timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
         override fun onTick(millisUntilFinished: Long) {
            _time.value = (millisUntilFinished/ ONE_SECOND)
            _hasTimerStopped.value = false
            Log.d("Time:", DateUtils.formatElapsedTime(_time.value!!))
         }

         override fun onFinish() {
            _time.value = DONE
            _hasTimerStopped.value = true
            this.cancel()
         }
      }

      this.hasTimerStopped.observeForever {hasFinished ->
         if (hasFinished) {
            GlobalScope.launch(Dispatchers.IO) {
               cacheDatabase.cacheDao().deleteAll()
            }
         }
      }
   }

   private fun insertIntoCache(time: String, cocktailList: CocktailList) {
      val cache = Cache(null,time,converter.fromCocktailList(cocktailList.Cocktails))
      GlobalScope.launch(Dispatchers.IO) {
         cacheDatabase.cacheDao().addToCache(cache)
      }
      Log.d(TAG,"Successfully added to cache")
      timer.start()
   }

   private fun startLoading(){
      val action = HomeActivityAction.LoadingStarted
      store.dispatch(action)
   }

   private fun successfulLoad(cocktailList: CocktailList){
      val action = HomeActivityAction.LoadingSuccess(cocktailList)
      this.cocktailList = cocktailList
      store.dispatch(action)
   }

   private fun failedLoad(error:Throwable){
      val action = HomeActivityAction.LoadingFailure(error)
      store.dispatch(action)
   }

   fun loadFilteredData(query: String?){
      GlobalScope.launch(Dispatchers.IO) {
         var tempCocktailList = mutableListOf<Cocktail>()
         val action: HomeActivityAction = if (query != null) {
            for (cocktail in cocktailList.Cocktails) {
               if (cocktail.drinkName.lowercase().contains(query.lowercase()) || cocktail.drinkInstruction.lowercase().contains(query.lowercase())) {
                  tempCocktailList.add(cocktail)
               }
            }
            HomeActivityAction.LoadFilteredList(CocktailList(tempCocktailList))
         } else {
            HomeActivityAction.LoadFilteredList(CocktailList(emptyList()))
         }
         store.dispatch(action)
      }
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
         else {
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
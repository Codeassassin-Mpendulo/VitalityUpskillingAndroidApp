package com.example.cocktaildictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktaildictionary.databinding.ActivityMainBinding
import com.example.cocktaildictionary.network.Cocktail
import com.example.cocktaildictionary.network.CocktailApiServices
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.network.RetrofitClientInstance
import com.example.cocktaildictionary.utils.CocktailAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cocktails: List<Cocktail>
    private lateinit var cocktailAdapter: CocktailAdapter
    private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getPopularCocktails()
    }


    private fun getPopularCocktails() {
        val service = RetrofitClientInstance.retrofitInstance?.create(CocktailApiServices::class.java)

        myCompositeDisposable.add((service?.getCocktails()?: Observable.just(CocktailList(emptyList<Cocktail>())))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { res ->

                    dataReceivedUITransition()
                    cocktails = res.Cocktails
                    binding.cocktailRecyclerview.setHasFixedSize(true)
                    cocktailAdapter = CocktailAdapter(cocktails, this)
                    binding.cocktailRecyclerview.adapter = cocktailAdapter
                    binding.cocktailRecyclerview.layoutManager = LinearLayoutManager(this)
                },
                { throwable ->
                    dataNotReceivedUITransition()
                    binding.Id.text = "Failure: "+throwable.message.toString()
                    binding.retry.setOnClickListener{
                        retryUITransition()
                        getPopularCocktails()
                    }
                }
            )
        )
    }

    private fun dataReceivedUITransition(){
        binding.getData.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.retry.visibility = View.INVISIBLE
        binding.cocktailRecyclerview.visibility = View.VISIBLE
    }

    private fun dataNotReceivedUITransition(){
        binding.Id.visibility = View.VISIBLE
        binding.getData.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.retry.visibility = View.VISIBLE
    }

    private fun retryUITransition(){
        binding.Id.visibility = View.INVISIBLE
        binding.retry.visibility = View.INVISIBLE
        binding.getData.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }
}
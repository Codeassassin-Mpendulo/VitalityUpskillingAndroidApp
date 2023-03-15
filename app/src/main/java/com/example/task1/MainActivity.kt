package com.example.task1


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.task1.databinding.ActivityMainBinding
import com.example.task1.network.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var Cocktails: List<Cocktail>

    private var myCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getPopularCocktails()
    }


    private fun getPopularCocktails() {
        val service =
            RetrofitClientInstance.retrofitInstance?.create(CocktailApiServices::class.java)

        myCompositeDisposable.add(service!!.getCocktails()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { res ->
                    binding.getData.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.Id.visibility = View.VISIBLE
                    binding.Id.text = "Success: Found " + res.Cocktails.size.toString() + " Cocktails"
                },
                { throwable ->
                    binding.getData.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.Id.visibility = View.VISIBLE
                    binding.Id.text = "Failure: "+throwable.message.toString()
                }
            )
        )

    }


}





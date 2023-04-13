package com.example.cocktaildictionary.views

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktaildictionary.databinding.ActivityMainBinding
import com.example.cocktaildictionary.network.CocktailList
import com.example.cocktaildictionary.state.HomeActivityStates
import com.example.cocktaildictionary.utils.CocktailAdapter
import com.example.cocktaildictionary.viewmodel.HomeActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cocktailAdapter: CocktailAdapter
    private lateinit var viewModel:HomeActivityViewModel
    private var menuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]

        lifecycleScope.launchWhenResumed{
            viewModel.viewState.collect{ viewState ->
                processViewState(viewState)
            }
        }
    }

    private fun processViewState(viewState: HomeActivityStates){

        when (viewState) {
            HomeActivityStates.Loading -> {
                retryUITransition()
                loadingData()
                viewModel.getPopularCocktails()
                return
            }
            HomeActivityStates.DataLoadFail(error = viewModel.error?: Throwable()) -> {
                dataNotReceivedUITransition()
                binding.Id.text = "Failure: "+ viewModel.error?.message.toString()
                binding.retry.setOnClickListener{
                    retryUITransition()
                    loadingData()
                    viewModel.getPopularCocktails()
                }
                return
            }
            HomeActivityStates.DataLoadSuccessful(cocktailList = viewModel.cocktailList?: CocktailList(Cocktails = emptyList())) -> {
                dataReceivedUITransition()
                binding.cocktailRecyclerview.setHasFixedSize(true)
                cocktailAdapter = CocktailAdapter((viewModel.cocktailList?:CocktailList(emptyList())).Cocktails,this)
                binding.cocktailRecyclerview.adapter = cocktailAdapter
                binding.cocktailRecyclerview.layoutManager = LinearLayoutManager(this)
                return
            }
        }
    }

    private fun dataReceivedUITransition(){
        binding.getData.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.retry.visibility = View.INVISIBLE
        binding.cocktailRecyclerview.visibility = View.VISIBLE
        if(menuItem != null){
            menuItem?.isVisible = true
        }
    }

    private fun dataNotReceivedUITransition(){
        binding.Id.visibility = View.VISIBLE
        binding.getData.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.retry.visibility = View.VISIBLE
        if(menuItem == null){
            menuItem?.isVisible = false
        }
    }

    private fun retryUITransition(){
        binding.Id.isVisible = false
        binding.retry.isVisible = false
    }

    private fun loadingData(){
        binding.getData.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }
}
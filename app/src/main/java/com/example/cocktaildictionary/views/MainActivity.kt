package com.example.cocktaildictionary.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktaildictionary.R
import com.example.cocktaildictionary.databinding.ActivityMainBinding
import com.example.cocktaildictionary.state.HomeActivityStates
import com.example.cocktaildictionary.utils.CocktailAdapter
import com.example.cocktaildictionary.viewmodel.HomeActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var cocktailAdapter : CocktailAdapter
    private lateinit var viewModel : HomeActivityViewModel
    private lateinit var menuItem : MenuItem
    private lateinit var globalMenu: Menu

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        globalMenu = menu
        menuItem = menu.findItem(R.id.search)

        val searchView: SearchView = menuItem.actionView as (SearchView)
        searchView.queryHint = "Type here to search"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(newText: String?): Boolean {
                viewModel.loadFilteredData(newText)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.loadFilteredData(newText)
                return true
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshApp(binding.swipeRefreshLayout,menuItem)
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun processViewState(viewState: HomeActivityStates){

        when (viewState) {
            is HomeActivityStates.Loading -> {
                hideRetryUI()
                showLoadingUI()
                viewModel.getPopularCocktails()
                return
            }
            is HomeActivityStates.DataLoadFail -> {
                dataNotReceivedUITransition()
                binding.Id.text = "Failure: "+ viewState.error.message.toString()
                binding.retry.setOnClickListener{
                    hideRetryUI()
                    showLoadingUI()
                    viewModel.getPopularCocktails()
                }
                return
            }
            is HomeActivityStates.DataLoadSuccessful -> {
                dataReceivedUITransition()
                binding.cocktailRecyclerview.setHasFixedSize(true)
                cocktailAdapter = CocktailAdapter(viewState.cocktailList.Cocktails,this)
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
//        menuItem.isVisible = true
    }

    private fun dataNotReceivedUITransition(){
        binding.Id.visibility = View.VISIBLE
        binding.getData.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.retry.visibility = View.VISIBLE
    }

    private fun hideRetryUI(){
        binding.Id.isVisible = false
        binding.retry.isVisible = false
    }

    private fun showLoadingUI(){
        binding.getData.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }
}
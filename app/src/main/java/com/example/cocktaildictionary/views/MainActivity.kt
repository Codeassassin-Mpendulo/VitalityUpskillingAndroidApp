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
import com.example.cocktaildictionary.state.LoadingViewState
import com.example.cocktaildictionary.utils.CocktailAdapter
import com.example.cocktaildictionary.viewmodel.LoadingViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cocktailAdapter: CocktailAdapter
    private lateinit var viewModel:LoadingViewModel
    private var menuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoadingViewModel::class.java]
        lifecycleScope.launchWhenResumed{
            viewModel.viewState.collect{ viewState ->
                processViewState(viewState)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu,menu)
        menuItem = menu?.findItem(R.id.search)
        val searchView: SearchView = menuItem?.actionView as (SearchView)
        searchView.queryHint = "Type here to search"

        if(viewModel.isCocktailListNull()){
            this.invalidateOptionsMenu()
            menuItem?.isVisible = false
        }

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
        return super.onCreateOptionsMenu(menu)
    }

    private fun processViewState(viewState: LoadingViewState){
        if (viewState.showHeading && viewState.showProgressCircle&&viewState.data==null){
            retryUITransition()
            loadingData()
            viewModel.getPopularCocktails()
            return
        }
        else if(!viewState.showHeading && !viewState.showProgressCircle && viewState.data==null && viewState.error!=null){
            dataNotReceivedUITransition()
            binding.Id.text = "Failure: "+ viewState.error.message.toString()
            binding.retry.setOnClickListener{
                retryUITransition()
                loadingData()
                viewModel.getPopularCocktails()
            }
        }
        else if(!viewState.showHeading && !viewState.showProgressCircle && viewState.data!=null){
            dataReceivedUITransition()
            binding.cocktailRecyclerview.setHasFixedSize(true)
            cocktailAdapter = CocktailAdapter(viewState.data.Cocktails,this)
            binding.cocktailRecyclerview.adapter = cocktailAdapter
            binding.cocktailRecyclerview.layoutManager = LinearLayoutManager(this)
            return
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
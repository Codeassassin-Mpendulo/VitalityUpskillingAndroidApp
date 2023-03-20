package com.example.task1.views


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.task1.databinding.ActivityMainBinding
import com.example.task1.state.LoadingViewState
import com.example.task1.utils.CocktailAdapter
import com.example.task1.viewmodel.LoadingViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cocktailAdapter: CocktailAdapter
    private lateinit var viewModel:LoadingViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoadingViewModel::class.java)
        lifecycleScope.launchWhenResumed{
            viewModel.viewState.collect{ viewState ->
                processViewState(viewState)
            }
        }

    }

    private fun processViewState(viewState: LoadingViewState){

        if (viewState.showHeading && viewState.showProgressCircle&&viewState.data==null){
            loadingData()
            viewModel.getPopularCocktails()
            return
        }
        else if(!viewState.showHeading && !viewState.showProgressCircle && viewState.data==null && viewState.error!=null){
            dataNotReceivedUITransition()
            binding.Id.text = "Failure: "+viewState.error?.message.toString()
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

    }
    private fun loadingData(){
        binding.getData.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }





}





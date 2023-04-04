package com.example.cocktaildictionary.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktaildictionary.databinding.EachCocktailBinding
import com.example.cocktaildictionary.network.Cocktail
import com.squareup.picasso.Picasso

class CocktailAdapter(private val cocktailList: List<Cocktail>,private val context:Context): RecyclerView.Adapter<CocktailAdapter.ViewHolderClass>() {


    inner class ViewHolderClass(val itemBinding: EachCocktailBinding): RecyclerView.ViewHolder(itemBinding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        return ViewHolderClass(EachCocktailBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        var data = cocktailList[position]
        val imageURL = data.img
//        val id = data.id
        val cocktailName = data.drinkName
        val cocktailInstruction = data.drinkInstruction


        Picasso.get().load(imageURL).into(holder.itemBinding.drinkImg);
        holder.itemBinding.drinkName.text = cocktailName

        holder.itemBinding.viewHolder.setOnClickListener{
            Toast.makeText(context,cocktailInstruction,Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return cocktailList.size
    }
}
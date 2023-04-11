package com.example.cocktaildictionary.network

import com.google.gson.annotations.SerializedName

data class Cocktail(
    @SerializedName("idDrink") val id: String,
    @SerializedName("strDrinkThumb") val img : String,
    @SerializedName("strDrink") val drinkName: String,
    @SerializedName("strInstructions") val drinkInstruction:String
)



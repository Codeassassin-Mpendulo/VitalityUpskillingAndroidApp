package com.example.task1.network
import com.google.gson.annotations.SerializedName

data class CocktailList(
    @SerializedName("drinks") var Cocktails: List<Cocktail>
) {
}
package com.example.task1.network


import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Headers




interface CocktailApiServices {

    @Headers(
        "X-RapidAPI-Key: a6c81ef7b4mshc75f5c4f3777f74p15f981jsn3d4bf099cca7",
        "X-RapidAPI-Host: the-cocktail-db.p.rapidapi.com"
    )

    @GET("popular.php")
    fun getCocktails():
            Observable<CocktailList>
}


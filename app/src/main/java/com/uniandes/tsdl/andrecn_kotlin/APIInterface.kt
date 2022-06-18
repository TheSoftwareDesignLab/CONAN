package com.uniandes.tsdl.andrecn_kotlin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIInterface {
    @GET("movies/")
    fun getMovies() : Call<List<Movie>>

    @POST("movies/")
    fun postMovies() : Call<List<Movie>>

    @HTTP(method = "GET", path = "movies/")
    fun getMoovies() : Call<List<Movie>>

    @PUT("movies/")
    fun putMovies() : Call<List<Movie>>

    @PATCH("movies/")
    fun patchMovies() : Call<List<Movie>>

    @DELETE("movies/")
    fun deleteMovies() : Call<List<Movie>>

    @OPTIONS("movies/")
    fun optionsMovies() : Call<List<Movie>>

    @HEAD("movies/")
    fun headMovies() : Call<List<Movie>>

    companion object {

        var BASE_URL = "https://android-eventual-connectivity.herokuapp.com/"

        fun create() : APIInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIInterface::class.java)

        }
    }
}
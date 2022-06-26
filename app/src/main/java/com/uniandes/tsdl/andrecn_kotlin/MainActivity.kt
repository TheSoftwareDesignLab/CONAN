package com.uniandes.tsdl.andrecn_kotlin


import android.app.job.JobScheduler
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: RecyclerAdapter

    private val client = OkHttpClient()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scheduler: JobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        val client = OkHttpClient()

        val client_2 = OkHttpClient()

        val inter = APIInterface.create()
        var apiInterface = inter.getMovies()
        apiInterface = inter.postMovies()
        apiInterface = inter.getMoovies()
        apiInterface = inter.putMovies()
        var appiInterface = inter.patchMovies()
        apiInterface = inter.deleteMovies()//optionsMovies
        apiInterface = inter.optionsMovies()
        apiInterface = inter.headMovies()

        recyclerView = this.findViewById(R.id.recyclerview)
        recyclerAdapter = RecyclerAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter

        val check = CheckNetworConnected()

        if(check.isCheckConnectivity(this)) {
            apiInterface.enqueue(object : Callback<List<Movie>> {
                override fun onResponse(call: Call<List<Movie>>?, response: Response<List<Movie>>?)
                {
                    if (response?.body() != null)
                        recyclerAdapter.setMovieListItems(response.body()!!)
                }

                override fun onFailure(call: Call<List<Movie>>?, t: Throwable?) {
                    //aaa
                }
            })

            appiInterface.enqueue(object : Callback<List<Movie>> {
                override fun onResponse(
                    call: Call<List<Movie>>?,
                    response: Response<List<Movie>>?
                ) {

                }

                override fun onFailure(call: Call<List<Movie>>, t: Throwable)
                {
                    TODO()
                }


            })

            var loquesea = apiInterface.execute()
        }




    }

    fun test()
    {
        val inter = APIInterface.create()
        var apiInterface = inter.getMovies()
        apiInterface = inter.postMovies()
        apiInterface = inter.getMoovies()
        apiInterface = inter.putMovies()
        var appiInterface = inter.patchMovies()
        apiInterface = inter.deleteMovies()//opti

        appiInterface.enqueue(object : Callback<List<Movie>> {
            override fun onResponse(
                call: Call<List<Movie>>?,
                response: Response<List<Movie>>?
            ) {

            }

            override fun onFailure(call: Call<List<Movie>>?, t: Throwable?) {
                Toast.makeText(applicationContext, "message", Toast.LENGTH_LONG).show()

            }
        })
    }

    fun volleyTest(){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.google.com"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(com.android.volley.Request.Method.GET, url,
            com.android.volley.Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.

            },
            com.android.volley.Response.ErrorListener {
                print(it.localizedMessage)
            })

        // Add the request to the RequestQueue.
        //queue.add(stringRequest)
        ///*
        queue.add(StringRequest(com.android.volley.Request.Method.GET, url,
            com.android.volley.Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.

            },
            com.android.volley.Response.ErrorListener {

            }))
        //*/
    }

    fun okHttpTest1(){

        fun run() {
            val request = Request.Builder()
                .url("https://publicobject.com/helloworld.txt")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body()!!.string())
            }
        }
    }

    fun okHttpTest2(){
        val request = Request.Builder()
            .url("http://publicobject.com/helloworld.txt")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    println(response.body()!!.string())
                }
            }
        })
    }

    fun javaNetTest(){
        try{
            val connection: HttpURLConnection = URL("http://www.google.com").openConnection() as HttpURLConnection
            connection.setRequestProperty("Accept-Charset", "utf-8")
            val resp = connection.connect()
        }
        catch (ex:Exception){

        }
    }
}
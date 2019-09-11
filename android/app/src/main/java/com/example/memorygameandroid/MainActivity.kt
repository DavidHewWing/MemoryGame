package com.example.memorygameandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val queue = Volley.newRequestQueue(this)
        val url = "https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6"
        val jsonArrayRequest = JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener { response ->
                val productArray: JSONArray = response.getJSONArray("products")
                for(i in 0 until productArray.length()){
                    val product: JSONObject = productArray.getJSONObject(i)
                    val title = product.getString("title")
                    val id = product.getLong("id").toString()
                    val imageUrl = product.getJSONArray("images").getJSONObject(0).getString("src")
                    Log.d("Taggy", "Title: $title: Id: $id. Image: $imageUrl")
                }
                Log.d("Taggy", productArray.length().toString())
            },
            Response.ErrorListener { error ->
                Log.d("Taggy", "Error Occurred!")
            }
        )
        queue.add(jsonArrayRequest)
    }
}

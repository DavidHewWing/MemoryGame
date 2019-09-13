package com.example.memorygameandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class PlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val cardList = intent.getParcelableArrayListExtra<CardModel>("cardList")
    }
}

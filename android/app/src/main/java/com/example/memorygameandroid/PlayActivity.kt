package com.example.memorygameandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val cardList = intent.getParcelableArrayListExtra<CardModel>("cardList")

        bottom_navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        loadFragment(bottom_navigation.selectedItemId)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        loadFragment(item.itemId)
        true
    }

    private fun loadFragment(itemId: Int) {
        val tag = itemId.toString()
        var fragment = supportFragmentManager.findFragmentByTag(tag) ?: when (itemId) {
            R.id.setup_menu -> {
                SetUpFragment.newInstance()
            }
            R.id.play_menu -> {
                GameFragment.newInstance()
            }
            else -> {
                null
            }
        }
        // replace fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        }
    }
}

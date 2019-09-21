package com.example.memorygameandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    private lateinit var cardMap: HashMap<Long, CardModel>
    private val gameFrag = GameFragment.newInstance()
    private val setupFrag = SetUpFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        cardMap = intent.getSerializableExtra("cardMap") as HashMap<Long, CardModel>
        val bundle = Bundle()
        bundle.putSerializable("cardMap", cardMap)
        gameFrag.arguments = bundle

        bottom_navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        loadFragment(bottom_navigation.selectedItemId)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        loadFragment(item.itemId)
        true
    }

    fun loadFragment(itemId: Int) {
        val tag = itemId.toString()
        var fragment = supportFragmentManager.findFragmentByTag(tag) ?: when (itemId) {
            R.id.setup_menu -> {
                setupFrag
            }
            R.id.play_menu -> {
                gameFrag
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

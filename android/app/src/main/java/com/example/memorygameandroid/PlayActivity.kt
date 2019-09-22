package com.example.memorygameandroid

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog.onPreShow
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.card_in_hand.*
import kotlinx.android.synthetic.main.fragment_game.*

class PlayActivity : AppCompatActivity() {

    private val gameFrag = GameFragment.newInstance()
    private val setupFrag = SetUpFragment.newInstance()
    private var updatedHandIndex = 0

    private lateinit var dialog: MaterialDialog
    private lateinit var handList: ArrayList<CardModel>
    private var pairsCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val cardList = intent.getParcelableArrayListExtra<CardModel>("cardList")
        val bundle = Bundle()
        bundle.putParcelableArrayList("cardList", cardList)
        gameFrag.arguments = bundle

        dialog  = MaterialDialog(this, BottomSheet())
        dialog.cornerRadius(16f)
        dialog.customView(R.layout.card_in_hand)
        dialog.setPeekHeight(700)

        bottom_navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        loadFragment(bottom_navigation.selectedItemId)

        val model = ViewModelProviders.of(this).get(Communicator::class.java)
        model.message.observe(this,
            Observer<Any> { o ->
                if (o is ArrayList<*>) {
                    handList = o as ArrayList<CardModel>
                    if (pairsCount == handList.size || handList.size < 1) {
                        resetHand()
                    } else {
                        updateHand()
                    }
                }
                if(o is HashMap<*,*>) {
                    pairsCount = o["pairCount"]!! as Int
                }
            }
        )

    }

    private fun updateHand() {
            val view = dialog.getCustomView() as RelativeLayout
            val card = view.getChildAt(updatedHandIndex) as MaterialCardView
            val frameLayout = card.getChildAt(0) as FrameLayout
            val constraintLayout = frameLayout.getChildAt(0) as ConstraintLayout
            val iView = constraintLayout.getChildAt(0) as ImageView
            Picasso.get().load(handList[updatedHandIndex].imageUrl).into(iView)
            updatedHandIndex++
    }

    private fun resetHand() {
        for(i in 0 until 4) {
            val view = dialog.getCustomView() as RelativeLayout
            val card = view.getChildAt(i) as MaterialCardView
            val frameLayout = card.getChildAt(0) as FrameLayout
            val constraintLayout = frameLayout.getChildAt(0) as ConstraintLayout
            val iView = constraintLayout.getChildAt(0) as ImageView
            iView.setImageDrawable(resources.getDrawable(R.drawable.shopify, theme))
        }
        updatedHandIndex = 0
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        loadFragment(item.itemId)
        when (item.itemId) {
            R.id.cards_menu -> {
                dialog.show()
            }
        }
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

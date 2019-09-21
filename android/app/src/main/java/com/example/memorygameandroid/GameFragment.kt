package com.example.memorygameandroid

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private var numColumns = 5 // how many cards
    private var numRows = 0 // how many rows
    private var remainders = 0 // how many leftover
    private var totalCells = 0
    private var pairsCount = 0
    private val gameMap = HashMap<Int, CardModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val cardMap = arguments?.getSerializable("cardMap")
        Log.d("cardmappo", cardMap.toString())
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model=ViewModelProviders.of(activity!!).get(Communicator::class.java)

        model.message.observe(this,
            Observer<Any> { o ->
                val total = (o as HashMap<String, Int>)["pairCount"]?.times(o["winningCount"]!!)
                if (total != null) {
                    numRows = total / numColumns
                    remainders = total - (numRows * numColumns)
                    totalCells = total
                    pairsCount = o["pairCount"]!!
                    setUpTable()
                }
            }
        )
    }

    /**
     * @params drawRes - the drawable you want as the card logo
     * @return imageView - the imageView it creates
     */
    private fun createCardLogo(drawRes: Int): ImageView {
        val imageView = ImageView(context)
        imageView.setImageDrawable(resources.getDrawable(drawRes, context?.theme))
        imageView.setPadding(40, 40,40,40)
        return imageView
    }

    /**
     * @params shape - the shape of the card
     * @params isLastRow - if you are adding the cards in the last row or not
     * @params width - width of the card
     * @params height - height of the card
     * @return the relative layout (card back)
     */
    private fun createCardBack(shape: GradientDrawable, isLastRow: Boolean, width: Int, height: Int) : RelativeLayout {
        val relativeLayout = RelativeLayout(context)
        val lastRowLayout = TableRow.LayoutParams(width, height, 0F)
        val defaultRowLayout = TableRow.LayoutParams(width, height, 1F)
        if (isLastRow) {
            relativeLayout.apply {
                layoutParams = lastRowLayout
                background = shape
                elevation = 10F
                translationZ = 10F
            }
        } else {
            relativeLayout.apply {
                layoutParams = defaultRowLayout
                background = shape
                elevation = 10F
                translationZ = 10F
            }
        }
        (relativeLayout.layoutParams as LinearLayout.LayoutParams).setMargins(10,10, 10, 10)
        return relativeLayout
    }

    private fun setUpCardInfo() {
        val amountOfPairs = totalCells / pairsCount
        for (i in 0 until amountOfPairs) {

        }
}

    /**
     * @params layoutheight - the height of the parent layout
     * @params layoutwidth - the width of the parent layout
     */
    private fun displayTable(layoutheight: Int, layoutwidth:Int) {
        // adding shadow and shape to the card
        var count = 0
        val shape = GradientDrawable()
        shape.cornerRadius = 8F
        shape.setColor(Color.parseColor("#b9dab8"))
        (layout.parent as ViewManager).removeView(layout)

        // adding the click listener
        val onClickListener = View.OnClickListener{v ->
            Log.d("XDDD", "You touched a relative layout")
        }

        // getting the height and width of each row
        val numRowsWithRemainder = if(remainders == 0) this.numRows else this.numRows + 1
        val height = layoutheight / numRowsWithRemainder
        val width = layoutwidth / this.numColumns

        // adding the views to the screen and the table layout
        for(i in 0 until numRowsWithRemainder) {
            val row = TableRow(context)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            for (j in 0 until this.numColumns) {
                if (count == totalCells ) {
                    break
                }
                // make the card
                val imageView = createCardLogo(R.drawable.shopify)
                val relativeLayout =
                    if (totalCells - count <= remainders) createCardBack(shape, true, width, height)
                    else createCardBack(shape, false, width, height)
                // finally add all to screen
                relativeLayout.addView(imageView)
                relativeLayout.setOnClickListener(onClickListener)
                row.addView(relativeLayout)
                count++
            }
            cardLayout?.addView(row)
        }
        setUpCardInfo()
    }

    private fun setUpTable() {
        val vto = parentLinearLayout.viewTreeObserver
        vto.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                parentLinearLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if(parentLinearLayout.measuredHeight != 0 && parentLinearLayout.measuredWidth != 0) {
                    displayTable(parentLinearLayout.height - parentLinearLayout.marginBottom, parentLinearLayout.width)
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GameFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}


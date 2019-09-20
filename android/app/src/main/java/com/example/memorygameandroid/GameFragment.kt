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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
                    setUpTable()
                }
            }
        )
    }

    /**
     * @params layoutheight - the height of the parent layout
     * @params layoutwidth - the width of the parent layout
     */
    private fun displayTable(layoutheight: Int, layoutwidth:Int) {
        var count = 0
        val shape = GradientDrawable()
        shape.cornerRadius = 8F
        shape.setColor(Color.parseColor("#b9dab8"))
        (layout.parent as ViewManager).removeView(layout)
        val numRowsWithRemainder = if(remainders == 0) this.numRows else this.numRows + 1
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = layoutheight / numRowsWithRemainder
        val width = layoutwidth / this.numColumns
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
                val imageView = ImageView(context)
                imageView.setImageDrawable(resources.getDrawable(R.drawable.shopify, context?.theme))
                imageView.setPadding(40, 40,40,40)
                val relativeLayout = RelativeLayout(context)
                relativeLayout.apply {
                    layoutParams = TableRow.LayoutParams(width, height, 1.0F)
                    background = shape
                    elevation = 10F
                    translationZ = 10F
                }
                (relativeLayout.layoutParams as LinearLayout.LayoutParams).setMargins(10,10, 10, 10)
                relativeLayout.addView(imageView)
                row.addView(relativeLayout)
                count++
            }
            cardLayout?.addView(row)
        }
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


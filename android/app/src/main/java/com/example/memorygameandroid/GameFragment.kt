package com.example.memorygameandroid

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

    private fun displayTable(layoutheight: Int, layoutwidth:Int) {
        var count = 0
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
                val tv = TextView(context)
                tv.apply {
                    gravity = Gravity.CENTER
                    layoutParams = TableRow.LayoutParams(width, height, 1.0F)
                    text = "HAHA"
                    setBackgroundColor((Color.parseColor("#000000")))
                }
                (tv.layoutParams as LinearLayout.LayoutParams).setMargins(10,10, 10, 10)
                row.addView(tv)
                count++
            }
            wordLayout?.addView(row)
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


package com.example.memorygameandroid

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.view.marginBottom
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private var numColumns = 5 // how many cards
    private var numRows = 0 // how many rows
    private var remainders = 0 // how many leftover
    private var totalCells = 0
    private var pairsCount = 0
    private val gameMap = HashMap<Int, CardModel>()

    private lateinit var cardList: ArrayList<CardModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        cardList = arguments?.getParcelableArrayList<CardModel>("cardList") as ArrayList<CardModel>
        Log.d("cardmappo", cardList.toString())
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model = ViewModelProviders.of(activity!!).get(Communicator::class.java)

        model.message.observe(this,
            Observer<Any> { o ->
                val total = (o as HashMap<String, Int>)["pairCount"]?.times(o["winningCount"]!!)
                if (total != null) {
                    numRows = total / numColumns
                    remainders = total - (numRows * numColumns)
                    totalCells = total
                    pairsCount = o["pairCount"]!!
                    setUpCardInfo()
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
        imageView.setPadding(40, 40, 40, 40)
        return imageView
    }

    /**
     * @params shape - the shape of the card
     * @params isLastRow - if you are adding the cards in the last row or not
     * @params width - width of the card
     * @params height - height of the card
     * @return the relative layout (card back)
     */
    private fun createCardBack(
        shape: GradientDrawable,
        isLastRow: Boolean,
        width: Int,
        height: Int
    ): RelativeLayout {
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
        (relativeLayout.layoutParams as LinearLayout.LayoutParams).setMargins(10, 10, 10, 10)
        return relativeLayout
    }

    /**
     * Associates every card with a number which corresponds to a card in the game
     */
    private fun setUpCardInfo() {
        val amountOfPairs = totalCells / pairsCount
        val cardsInGame = arrayOfNulls<CardModel>(amountOfPairs)
        val cardsInGameIndexes = arrayListOf<Int>()
        val occurences = IntArray(cardsInGame.size) { i -> 0 }
        val exclude = arrayListOf<Int>()
        for (i in 0 until amountOfPairs) {
            val randomCardIndex = (0 until cardList.size).random()
            cardsInGame[i] = cardList[randomCardIndex]
            cardsInGameIndexes.add(i)
        }
        for (i in 0 until totalCells) {
            val cardRange = IntArray(cardsInGame.size) { i -> i }
            val indexesLeft = cardRange.filter { it -> it !in exclude }
            val randomIndex = indexesLeft.random()
            occurences[randomIndex] += 1
            if (occurences[randomIndex] == pairsCount) {
                exclude.add(randomIndex)
            }
            gameMap[i] = cardsInGame[randomIndex]!!
        }
    }

    // Sets up the click variable for each card
    private fun setupClickListeners( index: Int) : View.OnClickListener {
        return View.OnClickListener {
            val (title, id, imageUrl) = gameMap[index]!!
            Log.d("clicklistener", "You have touch card $title with id: $id at index $index with imageUrl $imageUrl")
        }
    }

    /**
     * @params layoutheight - the height of the parent layout
     * @params layoutwidth - the width of the parent layout
     */
    private fun displayTable(layoutheight: Int, layoutwidth: Int) {
        // adding shadow and shape to the card
        var count = 0
        val shape = GradientDrawable()
        shape.cornerRadius = 8F
        shape.setColor(Color.parseColor("#b9dab8"))
        (layout.parent as ViewManager).removeView(layout)

        // getting the height and width of each row
        val numRowsWithRemainder = if (remainders == 0) this.numRows else this.numRows + 1
        val height = layoutheight / numRowsWithRemainder
        val width = layoutwidth / this.numColumns

        // adding the views to the screen and the table layout
        for (i in 0 until numRowsWithRemainder) {
            val row = TableRow(context)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            for (j in 0 until this.numColumns) {
                if (count == totalCells) {
                    break
                }
                // make the card
                val imageView = createCardLogo(R.drawable.shopify)
                val relativeLayout =
                    if (totalCells - count <= remainders) createCardBack(shape, true, width, height)
                    else createCardBack(shape, false, width, height)
                val listener = setupClickListeners(count)
                // finally add all to screen
                relativeLayout.addView(imageView)
                relativeLayout.setOnClickListener(listener)
                row.addView(relativeLayout)
                count++
            }
            cardLayout?.addView(row)
        }
    }

    private fun setUpTable() {
        val vto = parentLinearLayout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                parentLinearLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (parentLinearLayout.measuredHeight != 0 && parentLinearLayout.measuredWidth != 0) {
                    displayTable(
                        parentLinearLayout.height - parentLinearLayout.marginBottom,
                        parentLinearLayout.width
                    )
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


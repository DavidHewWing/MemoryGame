package com.example.memorygameandroid

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Layout
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.view.marginBottom
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {

    private var numColumns = 5 // how many cards
    private var numRows = 0 // how many rows
    private var remainders = 0 // how many leftover
    private var totalCells = 0
    private var pairsCount = 0
    private val gameMap = HashMap<Int, CardModel>()

    private lateinit var cardList: ArrayList<CardModel>

    private var currentAnimator: Animator? = null
    private var shortAnimationDuration: Int = 0

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

    /**
     * Toggles the clicking of the cards
     * @param clickable - true if clickable, false otherwise
     * @param alphaValue - the value on how opaque/transparent to make the container
     */
    private fun toggleCardClick(clickable: Boolean, alphaValue: Float) {
        container.alpha = alphaValue
        for(i in 0 until cardLayout.childCount) {
            val interLayout = cardLayout.getChildAt(i) as TableRow
            for(j in 0 until interLayout.childCount) {
                val childLayout = interLayout.getChildAt(j)
                childLayout.isClickable = clickable
            }
        }
    }


    // Sets up the clicklistener for the cards
    private fun setupClickListeners( index: Int, relativeLayout: RelativeLayout) : View.OnClickListener {
        val clickListener = View.OnClickListener {view ->
            val (title, id, imageUrl) = gameMap[index]!!
            Log.d("clicklistener", "You have touch card $title with id: $id at index $index with imageUrl $imageUrl")
            zoomCard(relativeLayout, gameMap[index]!!)
        }
        return clickListener
    }

    private fun zoomCard(rLayout: RelativeLayout, cardModel: CardModel) {
        shortAnimationDuration = 500
        currentAnimator?.cancel()

        val (title, id, imageUrl) = cardModel

        cardIdTextView.text = id.toString()
        cardTitleTextView.text = title.toString()
        Picasso.get().load(imageUrl).into(cardImageView)

        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()
        rLayout.getGlobalVisibleRect(startBoundsInt)
        container.getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(globalOffset.x, globalOffset.y)
        finalBoundsInt.offset(globalOffset.x + 120, globalOffset.y - 70)

        val startBounds = RectF(startBoundsInt)
        val finalBounds = RectF(finalBoundsInt)

        val startScale: Float
        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
            // Extend start bounds horizontally
            startScale = startBounds.height() / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        hiddenCard.visibility = View.VISIBLE
        toggleCardClick(false, 0.4F)

        currentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(
                hiddenCard,
                View.X,
                startBounds.left,
                finalBounds.left)
            ).apply {
                with(ObjectAnimator.ofFloat(hiddenCard, View.Y, startBounds.top, finalBounds.top))
                with(ObjectAnimator.ofFloat(hiddenCard, View.SCALE_X, startScale, 1f))
                with(ObjectAnimator.ofFloat(hiddenCard, View.SCALE_Y, startScale, 1f))
            }
            duration = shortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    currentAnimator = null
                }
            })
            start()
        }

        Handler().postDelayed({
            currentAnimator?.cancel()
            currentAnimator = AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(hiddenCard, View.X, startBounds.left)).apply {
                    with(ObjectAnimator.ofFloat(hiddenCard, View.Y, startBounds.top))
                    with(ObjectAnimator.ofFloat(hiddenCard, View.SCALE_X, startScale))
                    with(ObjectAnimator.ofFloat(hiddenCard, View.SCALE_Y, startScale))
                }
                duration = shortAnimationDuration.toLong()
                interpolator = DecelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {
                        toggleCardClick(true, 1.0F)
                        hiddenCard.visibility = View.GONE
                        currentAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        toggleCardClick(true, 1.0F)
                        hiddenCard.visibility = View.GONE
                        currentAnimator = null
                    }
                })
                start()
            }
        }, 1500)

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
                val listener = setupClickListeners(count, relativeLayout)
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


# Memory Game

Add the click animation to zoom into your face and show data
Add playing after

Good idea to have max card count at 40
Here you are challenging yourself, it isn't a head-to-head matchup against another player


## Challenge

1. The user should have to find a minimum of 10 pairs to win. 
2. Keep track of how many pairs the user has found. 
3. When the user wins, display a message to let them know!
4. Make sure it compiles successfully.

Bonus:

1. Make the game configurable to match 3 or 4 of the same products instead of 2.
2. Make the grid size configurable. (The player needs to match more than 10 sets of the same product).
3. Build a slick screen that keeps track of the user’s score.
4. Make a button that shuffles the game.
5. Feel free to make the app beautiful and add anything else you think would be cool!

## Third Party Dependencies

### Volley
* Use volley by creating a RequestQueue (this manages worker threads) and passing it Request objects.

1. Create a request queue ```val queue = Volley.newRequestQueue(this)```
2. Create a string response from the URL 

```val stringRequest = StringRequest(Request.Method.GET, url,
        Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            textView.text = "Response is: ${response.substring(0, 500)}"
        },
        Response.ErrorListener { textView.text = "That didn't work!" }
```
  
 3. Add to request queue.
 
 Here with this application we are making a one-time request. So calling stop to destroy the RequestQueue.
 
 Most common use is to make the RequestQueue a singleton.
 


## How the game works

1. You choose amount of cards you want to match on the screen and how many cards define a match
2. You can see all the cards on the screen (small)
3. When you tap a card on the screen the enlarged picture comes up to you and you are forced to put it in your hand
4. The picture you just saw gets stored in your hand (which is accessible by a button in the toolbar which will open a dialog to show what you have picked)
5. If you don't have a match then it is the next person's turn
6. If you get a match the cards are eliminated and you get a point
7. Continue until you have no more cards

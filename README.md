# Memory Game


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
 

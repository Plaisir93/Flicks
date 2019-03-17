package com.pcp.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pcp.flicks.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    // constants
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API Key
    public final static String API_KEY_PARAM = "api_key";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    // instance fields
    AsyncHttpClient client;
    // the base url for loading images
    String imageBaseUrl;
    // the poster s ize to use when fetching images, part of the url
    String posterSize;
    // the list of currently playing movies
    ArrayList<Movie> movies;
    // the recycler view
    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // initialize the client
        client = new AsyncHttpClient();
        // initialize the list of movies
        movies = new ArrayList<>();
        // initialize the adapter --movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // get the configuration
        getConfiguration();
    }

    // get the list of currently playing movies from API
    private void getNowPlaying(){
        // create the url
        String url = API_BASE_URL + "/movie/now_playing";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API Key always required
        // execute a GET request expecting a JSON Object response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // load the results into movies list
                    JSONArray results = response.getJSONArray("results");
                    // iterate through result set and create Movie objects
                    for (int i=0; i<results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));

                } catch (JSONException e) {
                    logErrors("Failed to parse now_playing movies",e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logErrors("Failed getting data from now_playing endpoint", throwable, true);
            }
        });
    }
    // get the configuration from the API
    private void getConfiguration (){
        // create the url
        String url = API_BASE_URL + "/configuration";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API Key always required
        // execute a GET request expecting a JSON Object response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject images = response.getJSONObject("images");
                    // get the image url
                    imageBaseUrl = images.getString("secure_base_url");
                    // get the poster size
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
                    // use the option at index 3 or w342 as a fallback
                    posterSize = posterSizeOptions.optString(3, "w342");
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", imageBaseUrl, posterSize));
                    // get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logErrors("Failed parsing configuration",e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logErrors("Failed getting configuration", throwable, true);
            }
        });

    }

    // handle errors, log and alert user
    private void logErrors(String message, Throwable error, boolean alertUser){
        // always the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if(alertUser){
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}

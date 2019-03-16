package com.pcp.flicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    // constants
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name for the API Key
    public final static String API_KEY_PARAM = "api_key";
    // the API Key -- TODO move to a secure location
    public final static String API_KEY = "f027a1d88e5cb0553a7d5b62fc9a642";
    // tag for logging from this activity
    public final static String TAG = "MovieListActivity";
    // instance fields
    AsyncHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // initialize the client
        client = new AsyncHttpClient();
    }
    // get the configuration from the API
    private void getConfiguration (){
        // create the url
        String url = API_BASE_URL + "/configuration";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, API_KEY); // API Key always required
        // execute a GET request expecting a JSON Object response
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    // handle errors, log and alert user
    private void logErrors(String message, Throwable error, boolean alertUser){
        // always the error
        Log.e(TAG, message, error);
    }
}

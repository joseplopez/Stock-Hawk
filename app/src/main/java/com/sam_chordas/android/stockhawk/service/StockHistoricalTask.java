package com.sam_chordas.android.stockhawk.service;

import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockHistoricalTask extends AsyncTask<String, Void, Boolean> {
    private final String symbol;
    private String LOG_TAG = StockHistoricalTask.class.getSimpleName();
    public static int N_DAYS = 15;

    private StockDetailsListener listener;
    private ArrayList<String> data;

    public interface StockDetailsListener {
        void onDataRetrieved(ArrayList<String> data);
    }

    public StockHistoricalTask(String symbol, StockDetailsListener stockDetailsListener) {
        this.symbol = symbol;
        this.listener = stockDetailsListener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String resultJsonStr = null;
        try {
            Date today = new Date();
            Date firstDay = new Date(System.currentTimeMillis() - N_DAYS * 24 * 60 * 60 * 1000);
            String endDate = DateFormat.format("yyyy-MM-dd", today).toString();
            String startDate = DateFormat.format("yyyy-MM-dd", firstDay).toString();

            String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20" +
                    "symbol%20=%20%22" +
                    symbol +
                    "%22%20and%20" +
                    "startDate%20=%20%22" +
                    startDate +
                    "%22%20" +
                    "and%20" +
                    "endDate%20=%20%22" +
                    endDate +
                    "%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return false;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return false;
            }
            resultJsonStr = buffer.toString();
            data = getHistoricalValues(resultJsonStr);
            return true;

        } catch (IOException | JSONException e) {
            Log.d(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            listener.onDataRetrieved(data);
        } else {
            listener.onDataRetrieved(null);
        }
    }

    private ArrayList<String> getHistoricalValues(String moviesJsonStr) throws JSONException {

        ArrayList<String> result = new ArrayList<>();
        JSONArray values = new JSONObject(moviesJsonStr).getJSONObject("query").getJSONObject("results").getJSONArray("quote");

        for (int i = 0; i < values.length(); i++) {
            JSONObject stockValue = values.getJSONObject(i);
            result.add(stockValue.getString("Close"));
        }

        return result;
    }
}
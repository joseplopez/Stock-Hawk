package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockHistoricalTask extends AsyncTask<String, Void, Boolean>{
  private String LOG_TAG = StockHistoricalTask.class.getSimpleName();

  private OkHttpClient client = new OkHttpClient();
  private Context mContext;
  private StringBuilder mStoredSymbols = new StringBuilder();
  private boolean isUpdate;

  public StockHistoricalTask(){}

  @Override
  protected Boolean doInBackground(String... params) {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String resultJsonStr = null;
    String MOVIES_URL = "";
    try {
      // Construct the URL for the OpenWeatherMap query
      // Possible parameters are avaiable at OWM's forecast API page, at
      // http://openweathermap.org/API#forecast

      String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20=%20%22YHOO%22%20and%20startDate%20=%20%222009-09-11%22%20and%20endDate%20=%20%222010-03-10%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

      // Create the request to OpenWeatherMap, and open the connection
      urlConnection = (HttpURLConnection) new URL(url).openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // Read the input stream into a String
      InputStream inputStream = urlConnection.getInputStream();
      StringBuffer buffer = new StringBuffer();
      if (inputStream == null) {
        // Nothing to do.
        return null;
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
        return null;
      }
      resultJsonStr = buffer.toString();

    } catch (IOException e) {
      Log.e(LOG_TAG, "Error ", e);
      // If the code didn't successfully get the weather data, there's no point in attempting
      // to parse it.
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
    return null;
  }

  @Override
  protected void onPostExecute(Boolean result) {
    //showDialog("Downloaded " + result + " bytes");
  }

  /*@Override
  public int onRunTask(TaskParams params){
    Cursor initQueryCursor;
    if (mContext == null){
      mContext = this;
    }
    StringBuilder urlStringBuilder = new StringBuilder();
    try{
      // Base URL for the Yahoo query
      urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
      urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
        + "in (", "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    if (params.getTag().equals("init") || params.getTag().equals("periodic")){
      isUpdate = true;
      initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
          new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
          null, null);
      if (initQueryCursor.getCount() == 0 || initQueryCursor == null){
        // Init task. Populates DB with quotes for the symbols seen below
        try {
          urlStringBuilder.append(
              URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      } else if (initQueryCursor != null){
        DatabaseUtils.dumpCursor(initQueryCursor);
        initQueryCursor.moveToFirst();
        for (int i = 0; i < initQueryCursor.getCount(); i++){
          mStoredSymbols.append("\""+
              initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"))+"\",");
          initQueryCursor.moveToNext();
        }
        mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
        try {
          urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
    } else if (params.getTag().equals("add")){
      isUpdate = false;
      // get symbol from params.getExtra and build query
      String stockInput = params.getExtras().getString("symbol");
      try {
        urlStringBuilder.append(URLEncoder.encode("\""+stockInput+"\")", "UTF-8"));
      } catch (UnsupportedEncodingException e){
        e.printStackTrace();
      }
    }
    // finalize the URL for the API query.
    urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
        + "org%2Falltableswithkeys&callback=");
     String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20=%20%22YHOO%22%20and%20startDate%20=%20%222009-09-11%22%20and%20endDate%20=%20%222010-03-10%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

    String urlString;
    String getResponse;
    int result = GcmNetworkManager.RESULT_FAILURE;

    if (urlStringBuilder != null){
      urlString = urlStringBuilder.toString();
      try{
        getResponse = fetchData(url);
        result = GcmNetworkManager.RESULT_SUCCESS;
        try {
          ContentValues contentValues = new ContentValues();
          // update ISCURRENT to 0 (false) so new data is current
          if (isUpdate){
            contentValues.put(QuoteColumns.ISCURRENT, 0);
            mContext.getContentResolver().update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                null, null);
          }
          mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
              Utils.quoteJsonToContentVals(getResponse));
        }catch (RemoteException | OperationApplicationException e){
          Log.e(LOG_TAG, "Error applying batch insert", e);
        }
      } catch (IOException e){
        e.printStackTrace();
      }
    }

    return result;
  }*/

}
package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sam_chordas.android.stockhawk.listner.DataFetched;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nikhil on 3/23/16.
 */
public class FetchStockGraphData extends AsyncTask<String, Void, String> {

    private Context mContext;
    private String[] mLabels;
    private float[] mEntries;
    private String LOG_TAG = "StockGraphService";
    private DataFetched dataFetched;


    public FetchStockGraphData(DataFetched dataFetched) {
        this.dataFetched = dataFetched;

    }

    public String doInBackground(String... params) {
        String urlString = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22" +
                params[0] +
                "%22%20and%20startDate%20%3D%20%222016-01-01%22%20and%20endDate%20%3D%20%222016-03-23%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        urlConnection = null;
        try {
            Uri builtUri = Uri.parse(urlString);
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                //stream was empty
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                //buffer was empty
                return null;
            }
            return buffer.toString();
//            getGraphData(graphJsonStr);
        } catch (Exception e) {
            return null;
//            e.printStackTrace();
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


    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            getGraphData(s);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void getGraphData(String graphJsonStr) throws JSONException {
        JSONObject graphJson = new JSONObject(graphJsonStr);
        JSONObject results = graphJson.getJSONObject("query").getJSONObject("results");
        JSONArray resultsArray = results.getJSONArray("quote");
        int resultsLength = resultsArray.length();
        mEntries = new float[resultsLength];
        mLabels = new String[resultsLength];

        for (int i = 0; i < resultsLength; i++) {
            JSONObject current = resultsArray.getJSONObject(i);
            mEntries[i] = Float.parseFloat(current.getString("Close"));
            mLabels[i] = current.getString("Date");
        }


        if (mLabels != null && mEntries != null)
            dataFetched.onDataFetched(mLabels, mEntries);
    }

}

package com.sam_chordas.android.stockhawk.listner;

/**
 * Created by nikhil on 05/04/16.
 */
public interface DataFetched {

    void onDataFetched(String[] labels, float[] entries);
}

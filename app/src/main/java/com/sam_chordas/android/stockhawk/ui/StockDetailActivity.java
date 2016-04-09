package com.sam_chordas.android.stockhawk.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.BarSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.listner.DataFetched;
import com.sam_chordas.android.stockhawk.service.FetchStockGraphData;

/**
 * Created by nikhil on 3/13/16.
 */
public class StockDetailActivity extends AppCompatActivity implements DataFetched {
    private BarChartView mChart;
    private ProgressDialog progressDialog;
    private TextView xlabel;
    private TextView ylabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String symbol = intent.getStringExtra("symbol");

        setContentView(R.layout.activity_bar_graph);
        mChart = (BarChartView) findViewById(R.id.barChart);
        ylabel = (TextView) findViewById(R.id.ylabel);
        xlabel = (TextView) findViewById(R.id.xlabel);
        ylabel.setText(R.string.price);
        xlabel.setText(R.string.time);

        FetchStockGraphData service = new FetchStockGraphData(this);
        service.execute(symbol);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wait));
        progressDialog.show();

    }


    @Override
    public void onDataFetched(String[] labels, float[] entries) {


        float min = 0;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] > min) {
                min = entries[i];
            }
        }

        BarSet barSet = new BarSet(labels, entries);

        barSet.setColor(getResources().getColor(R.color.material_blue_500));

        mChart.addData(barSet);
        mChart.setBarSpacing(Tools.fromDpToPx(5));
        mChart.setRoundCorners(Tools.fromDpToPx(2));


        mChart.setBorderSpacing(Tools.fromDpToPx(20))
                .setYLabels(AxisController.LabelPosition.NONE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setAxisColor(getResources().getColor(R.color.material_blue_500))
                .setXAxis(true)
                .setYAxis(true);

        mChart.setBackgroundColor(getResources().getColor(android.R.color.white));
        mChart.show();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }
}

package com.sam_chordas.android.stockhawk.ui;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockHistoricalTask;

import java.util.ArrayList;
import java.util.Collections;

public class StockDetails extends AppCompatActivity {
    private LineChart mChart;
    ProgressDialog progress;
    private String symbol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        symbol = getIntent().getExtras().getString(MyStocksActivity.SYMBOL);

        mChart = (LineChart) findViewById(R.id.linechart);
        ((TextView) findViewById(R.id.title)).setText(symbol);
    }

    @Override
    public void onStart() {
        super.onStart();
        progress = ProgressDialog.show(this, getString(R.string.wait),
                getString(R.string.dowloading_data), true);

        new StockHistoricalTask(symbol, new StockHistoricalTask.StockDetailsListener() {
            @Override
            public void onDataRetrieved(ArrayList<String> data) {
                progress.dismiss();
                if (data != null) {
                    Collections.reverse(data);
                    setData(data);
                } else {
                    Toast.makeText(StockDetails.this, R.string.error_download, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }).execute();
    }


    private ArrayList<String> setXAxisValues(ArrayList<String> data) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < data.size(); i++) {
            xVals.add("");
        }
        return xVals;
    }


    // This is used to store Y-axis values
    private ArrayList<Entry> setYAxisValues(ArrayList<String> data) {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < data.size(); i++) {
            yVals.add(new Entry(Float.valueOf(data.get(i)), i));
        }
        return yVals;
    }

    private void setData(ArrayList<String> dataOnline) {
        ArrayList<String> xVals = setXAxisValues(dataOnline);

        ArrayList<Entry> yVals = setYAxisValues(dataOnline);

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, getString(R.string.stock_description_chart));
        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        // set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
        mChart.setDescription("");
        mChart.invalidate();

    }
}

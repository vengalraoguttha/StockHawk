package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import static android.R.attr.duration;
import static android.R.attr.x;

public class DetailActivity extends AppCompatActivity implements OnChartValueSelectedListener,SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.company_det)
    TextView compName;

    @BindView(R.id.price_det)
    TextView price;

    String history[];
    String company;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        company=intent.getStringExtra("SYMBOL");
        history=getHistory();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        setUpChart(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_char_setting),getString(R.string.key_week_1))));
    }

    public void setUpChart(int val){
        if(history!=null){
            chart.setTouchEnabled(true);
            chart.setScaleEnabled(true);
            chart.setHighlightPerTapEnabled(true);
            final ArrayList<String> quarters= new ArrayList<>();
            int i=0;
            List<Entry>  entries=new ArrayList<>();
            for (i=0;i<history.length;i++){
                String[] values=history[i].split(", ");
                entries.add(new Entry(i,(int)Float.parseFloat(values[1])));
                String dateString = new SimpleDateFormat("MMM d, ''yy", Locale.ENGLISH).format(new Date(Long.parseLong(values[0])));
                quarters.add(dateString);
                Log.v("date",dateString);
            }
            LineDataSet lineDataSet=new LineDataSet(entries,"Label");
            lineDataSet.setValueTextColor(R.color.colorAccent);
            LineData lineData=new LineData(lineDataSet);

            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return quarters.get((int)value);
                }

            };

            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setAxisMaximum(val-1);
            chart.setEnabled(false);

            YAxis yAxis=chart.getAxisLeft();
            yAxis.setTextColor(Color.WHITE);
            yAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return "$"+value;
                }
            });

            YAxis yAxisSec =chart.getAxisRight();
            yAxisSec.setTextColor(Color.WHITE);
            yAxisSec.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return "$"+value;
                }
            });

            chart.animateX(2000);
            chart.setData(lineData);
            chart.invalidate();

        }
    }

    public String[] getHistory(){

        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(company),null,null,null,null);

        if (cursor!=null){
            cursor.moveToFirst();


            compName.setText(cursor.getString(Contract.Quote.POSITION_SYMBOL));
            //set the content description
            compName.setContentDescription(getResources().getString(R.string.symbol_format,compName.getText()));

            setTitle(cursor.getString(Contract.Quote.POSITION_SYMBOL));

            DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

            history = cursor.getString(Contract.Quote.POSITION_HISTORY).split("\n");

            for(int i=0;i<history.length;i++){
                Log.v("history",history[i]);
            }

            price.setText(dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));
            cursor.close();
            return history;
        }

        return null;
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        price.setText(e.getY()+"");
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.chart_setting){
            Intent intent=new Intent(DetailActivity.this,Setting.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setUpChart(Integer.parseInt(sharedPreferences.getString(getString(R.string.key_char_setting),getString(R.string.key_week_1))));
    }
}

package org.sandix.glucometer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.sandix.glucometer.beans.GlBean;
import org.sandix.glucometer.beans.UserBean;
import org.sandix.glucometer.db.DB;

import java.text.DecimalFormat;
import java.util.List;


public class ActivityChart extends AppCompatActivity {

    private LineChart mChart;
    private UserBean userBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        getSupportActionBar().setTitle("Динамика пациента mmol/l");

        mChart = (LineChart) findViewById(R.id.lineChart);
        if(getIntent().hasExtra("userBean")){
            userBean = (UserBean)getIntent().getSerializableExtra("userBean");
            initializeChart();
            addEntries();
        }

    }

    private void addEntries() {
        LineData data = mChart.getData();
        if(data !=null){
            LineDataSet set = data.getDataSetByIndex(0);
            if(set == null){
                set = createSet();
                data.addDataSet(set);
            }

            DB db = new DB(this);
            db.open();
            List<GlBean> list = db.getGlBeansBySerial(userBean.getSerial_number());
            db.close();

            if(list!=null) {
                for (GlBean item : list) {
                    data.addXValue(item.getDate());

                    data.addEntry(new Entry(Float.parseFloat(String.valueOf(item.getGl_value())),
                                    set.getEntryCount()),
                            0);
                }
            }

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(3,12);
            mChart.moveViewToX(data.getXValCount() - 7);
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null,"Price");
        set.setDrawCubic(false);
        set.setCubicIntensity(0.3f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(3f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(15f);
        set.setValueFormatter(new MyValueFormatter());

        return set;
    }

    private void initializeChart(){
        mChart.setDescriptionTextSize(20f);
        mChart.setDescription(""); //@string/user_dynamics
        mChart.setNoDataText("Данные пользователя отсутствуют");

        //enable value hightlighting
        mChart.setHighlightPerDragEnabled(true);

        //enable touch gestures
        mChart.setTouchEnabled(true);

        //Scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);

        //enable pinch zoom to avoid scalling x and y axis separately
        mChart.setPinchZoom(false);

        //alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        //now, we work on data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add data to line chart
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1 = mChart.getXAxis();
        x1.setTextColor(Color.BLACK);
        x1.setDrawAxisLine(false);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(Color.GRAY);
        y1.setAxisMaxValue(30f);
        y1.setAxisMinValue(0f);
        y1.setTextSize(15f);

        YAxis yl2 = mChart.getAxisRight();
        yl2.setEnabled(false);

    }
}

class MyValueFormatter implements com.github.mikephil.charting.formatter.ValueFormatter{
    DecimalFormat mFormat;

    public MyValueFormatter(){
        mFormat = new DecimalFormat("#0.0");
    }
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }
}

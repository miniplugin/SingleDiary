package org.techtown.diary;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Fragment3 extends Fragment {

    //변수선언
    PieChart pieChart;
    BarChart barChart;
    LineChart lineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        initUI(rootView);
        return rootView;
    }

    public void initUI(ViewGroup rootView) {
        //변수에 값할당, 객체생성, 오브젝트생성, 인스턴스변수 생성, 실행가능한 변수 생성.
        pieChart = rootView.findViewById(R.id.chart1);
        //파이챠트 =================================================================
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("기분별 비율");
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(61f);//61각도 f실수형 숫자의미
        pieChart.setDrawCenterText(true);
        pieChart.setHighlightPerTapEnabled(true);
        Legend legend1 = pieChart.getLegend();
        legend1.setEnabled(false);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
        setData1();//데이터바인딩처리 외부 메서드호출
        //막대챠트 ================================================================
        barChart = rootView.findViewById(R.id.chart2);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(6, false);
        leftAxis.setAxisMinimum(0.0f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);
        Legend legend2 = barChart.getLegend();
        legend2.setEnabled(false);
        barChart.animateXY(1500, 1500);
        setData2();//데이터바인딩처리 외부 메서드호출
        //라인챠트 =====================================================================
        lineChart = rootView.findViewById(R.id.chart3);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        //대체 배경색 설정
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(0,0,0,0);
        //범례 가져 오기 (데이터 설정 후에 만 가능)
        Legend legend3 = lineChart.getLegend();
        legend3.setEnabled(false);
        XAxis xAxis3 = lineChart.getXAxis();
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis3.setTextSize(10f);
        xAxis3.setDrawAxisLine(false);
        xAxis3.setDrawGridLines(true);
        xAxis3.setTextColor(ColorTemplate.getHoloBlue());
        xAxis3.setCenterAxisLabels(true);
        xAxis3.setGranularity(1f);
        xAxis3.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("MM-DD", Locale.KOREA);

            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
        YAxis leftAxis3 = lineChart.getAxisLeft();
        leftAxis3.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis3.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis3.setDrawGridLines(true);
        leftAxis3.setGranularityEnabled(true);
        leftAxis3.setAxisMinimum(0f);
        leftAxis3.setAxisMaximum(170f);
        leftAxis3.setYOffset(-9f);
        //leftAxis3.setTextColor(Color.rgb(255,192,56));
        YAxis rightAxis3 = lineChart.getAxisRight();
        rightAxis3.setEnabled(false);
        setData3();//데이터바인딩처리 외부 메서드호출
    }

    private void setData3() {
        ArrayList<Entry> values = new ArrayList<>();
        values.add(new Entry(24f, 20.0f));
        values.add(new Entry(48f, 50.0f));
        values.add(new Entry(72f, 30.0f));
        values.add(new Entry(96f, 70.0f));
        values.add(new Entry(120f, 90.0f));
        //데이터 세트를 만들고 유형을 지정합니다.
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(true);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(ColorTemplate.getHoloBlue());
        set1.setDrawCircleHole(false);
        // 데이터 세트로 데이터 객체 생성
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        // 데이터가 챠트에 적용됨(아래)
        lineChart.setData(data);
        lineChart.invalidate();
    }

    private void setData2() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1.0f, 20.0f, getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new BarEntry(2.0f, 40.0f, getResources().getDrawable(R.drawable.smile2_24)));
        entries.add(new BarEntry(3.0f, 60.0f, getResources().getDrawable(R.drawable.smile3_24)));
        entries.add(new BarEntry(4.0f, 30.0f, getResources().getDrawable(R.drawable.smile4_24)));
        entries.add(new BarEntry(5.0f, 90.0f, getResources().getDrawable(R.drawable.smile5_24)));
        BarDataSet dataSet2 = new BarDataSet(entries, "요일별 기분");
        dataSet2.setColor(Color.rgb(240,120,124));
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c:ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        dataSet2.setColors(colors);
        dataSet2.setIconsOffset(new MPPointF(0, -10));
        BarData data = new BarData(dataSet2);//그래프에 사용될 데이터 객체생성
        data.setValueTextSize(10f);
        data.setDrawValues(false);
        data.setBarWidth(0.8f);
        barChart.setData(data);//여기서 데이터가 챠트에 적용됨.
        barChart.invalidate();//화면에 챠트가 나타난 이후엔 메모리에서 제거
    }

    private void setData1() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(10.0f, "",
                getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new PieEntry(30.0f, "",
                getResources().getDrawable(R.drawable.smile2_24)));
        entries.add(new PieEntry(20.0f, "",
                getResources().getDrawable(R.drawable.smile3_24)));
        entries.add(new PieEntry(20f, "",
                getResources().getDrawable(R.drawable.smile4_24)));
        entries.add(new PieEntry(20.0f, "",
                getResources().getDrawable(R.drawable.smile5_24)));
        PieDataSet dataSet = new PieDataSet(entries, "기분별 비율");
        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, -40));
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);//그래프에 사용될 데이터 객체생성
        data.setValueTextSize(22.0f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);//여기서 데이터가 챠트에 적용됨.
        pieChart.invalidate();//화면에 챠트가 나타난 이후엔 메모리에서 제거
    }

}
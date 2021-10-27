package org.techtown.diary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Fragment3 extends Fragment {
    //디버그용 태그 추가
    private static final String TAG = "태그Fragment3";
    //변수선언
    PieChart pieChart;
    BarChart barChart;
    LineChart lineChart;
    Context context;//현재 액티비티의 내용 콘텍스트 객체선언

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        initUI(rootView);
        loadChartData();//위 챠트UI에 외부 데이터 바인딩
        return rootView;
    }

    private void loadChartData() {
        NoteDatabase database = NoteDatabase.getInstance(context);
        // pieChart
        String sql = "select mood " +
                "  , count(mood) " +
                "from " + NoteDatabase.TABLE_NOTE + " " +
                "where create_date > '" + getMonthBefore(1) + "' " +
                "  and create_date < '" + getTomorrow() + "' " +
                "group by mood";
        Cursor cursor = database.rawQuery(sql);
        int recordCount = cursor.getCount();
        Log.d(TAG,"recordCount : " + recordCount);
        HashMap<String,Integer> dataHash1 = new HashMap<String,Integer>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String moodName = cursor.getString(0);
            int moodCount = cursor.getInt(1);
            Log.d(TAG,"#" + i + " -> " + moodName + ", " + moodCount);
            dataHash1.put(moodName, moodCount);
        }
        setData1(dataHash1);

        // barChart
        sql = "select strftime('%w', create_date) " +
                "  , avg(mood) " +
                "from " + NoteDatabase.TABLE_NOTE + " " +
                "where create_date > '" + getMonthBefore(1) + "' " +
                "  and create_date < '" + getTomorrow() + "' " +
                "group by strftime('%w', create_date)";

        cursor = database.rawQuery(sql);
        recordCount = cursor.getCount();
        Log.d(TAG,"recordCount : " + recordCount);
        HashMap<String,Integer> dataHash2 = new HashMap<String,Integer>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String weekDay = cursor.getString(0);
            int moodCount = cursor.getInt(1);
            Log.d(TAG,"#" + i + " -> " + weekDay + ", " + moodCount);
            dataHash2.put(weekDay, moodCount);
        }
        setData2(dataHash2);

        // lineChart
        sql = "select strftime('%Y-%m-%d', create_date) " +
                "  , avg(cast(mood as real)) " +
                "from " + NoteDatabase.TABLE_NOTE + " " +
                "where create_date > '" + getDayBefore(7) + "' " +
                "  and create_date < '" + getTomorrow() + "' " +
                "group by strftime('%Y-%m-%d', create_date)";
        cursor = database.rawQuery(sql);
        recordCount = cursor.getCount();
        Log.d(TAG,"recordCount : " + recordCount);
        HashMap<String,Integer> recordsHash = new HashMap<String,Integer>();
        for (int i = 0; i < recordCount; i++) {
            cursor.moveToNext();
            String monthDate = cursor.getString(0);
            int moodCount = cursor.getInt(1);
            Log.d(TAG,"#" + i + " -> " + monthDate + ", " + moodCount);
            recordsHash.put(monthDate, moodCount);
        }
        ArrayList<Float> dataKeys3 = new ArrayList<Float>();
        ArrayList<Integer> dataValues3 = new ArrayList<Integer>();
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            String monthDate = AppConstants.dateFormat5.format(cal.getTime());
            Object moodCount = recordsHash.get(monthDate);
            dataKeys3.add((i-6) * 24.0f);
            if (moodCount == null) {
                dataValues3.add(0);
            } else {
                dataValues3.add((Integer)moodCount);
            }
            Log.d(TAG,"#" + i + " -> " + monthDate + ", " + moodCount);
        }
        setData3(dataKeys3, dataValues3);
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
        //setData1();//데이터바인딩처리 외부 메서드호출 //외부메서드로 정리
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
        //setData2();//데이터바인딩처리 외부 메서드호출 //외부메서드로 정리
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
        //setData3();//데이터바인딩처리 외부 메서드호출 //외부메서드로 정리
    }

    private void setData3(ArrayList<Float> dataKeys3, ArrayList<Integer> dataValues3) {
        ArrayList<Entry> entries = new ArrayList<>();
        /* 라인챠트 더미데이터
        entries.add(new Entry(24f, 20.0f));
        entries.add(new Entry(48f, 50.0f));
        entries.add(new Entry(72f, 30.0f));
        entries.add(new Entry(96f, 70.0f));
        entries.add(new Entry(120f, 90.0f));
        */
        for (int i = 0; i < dataKeys3.size(); i++) {
            try {
                float outKey = dataKeys3.get(i);
                Integer outValue = dataValues3.get(i);
                Log.d(TAG,"#" + i + " -> " + outKey + ", " + outValue);
                entries.add(new Entry(outKey, new Float(outValue)));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //데이터 세트를 만들고 유형을 지정합니다.
        LineDataSet set1 = new LineDataSet(entries, "기분 변화");
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

    private void setData2(HashMap<String,Integer> dataHash2) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        /* 막대챠트 더미데이터
        entries.add(new BarEntry(1.0f, 20.0f, getResources().getDrawable(R.drawable.smile1_24)));
        entries.add(new BarEntry(2.0f, 40.0f, getResources().getDrawable(R.drawable.smile2_24)));
        entries.add(new BarEntry(3.0f, 60.0f, getResources().getDrawable(R.drawable.smile3_24)));
        entries.add(new BarEntry(4.0f, 30.0f, getResources().getDrawable(R.drawable.smile4_24)));
        entries.add(new BarEntry(5.0f, 90.0f, getResources().getDrawable(R.drawable.smile5_24)));
        */
        String[] keys = {"0", "1", "2", "3", "4", "5", "6"};
        int[] icons = {R.drawable.smile1_24, R.drawable.smile2_24,
                R.drawable.smile3_24, R.drawable.smile4_24,
                R.drawable.smile5_24};
        for (int i = 0; i < keys.length; i++) {
            float value = 0.0f;
            Integer outValue = dataHash2.get(keys[i]);
            Log.d(TAG,"#" + i + " -> " + outValue);
            if (outValue != null) {
                value = outValue.floatValue();
            }
            Drawable drawable = null;
            if (value <= 1.0f) {
                drawable = getResources().getDrawable(icons[0]);
            } else if (value <= 2.0f) {
                drawable = getResources().getDrawable(icons[1]);
            } else if (value <= 3.0f) {
                drawable = getResources().getDrawable(icons[2]);
            } else if (value <= 4.0f) {
                drawable = getResources().getDrawable(icons[3]);
            } else if (value <= 5.0f) {
                drawable = getResources().getDrawable(icons[4]);
            }
            entries.add(new BarEntry(Float.valueOf(String.valueOf(i+1)), value, drawable));
        }
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

    private void setData1(HashMap<String,Integer> dataHash1) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        /* 파이챠트 더미데이터
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
        */
        String[] keys = {"0", "1", "2", "3", "4"};
        int[] icons = {R.drawable.smile1_24, R.drawable.smile2_24,
                R.drawable.smile3_24, R.drawable.smile4_24,
                R.drawable.smile5_24};
        for (int i = 0; i < keys.length; i++) {
            int value = 0;
            Integer outValue = dataHash1.get(keys[i]);
            if (outValue != null) {
                value = outValue.intValue();//파이의 넓이값 구하기
            }
            if (value > 0) {
                entries.add(new PieEntry(value, "",
                        getResources().getDrawable(icons[i])));
            }
        }
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
    //오늘 날짜 구하기
    public String getToday() {
        Date todayDate = new Date();
        return AppConstants.dateFormat5.format(todayDate);
    }
    //내일 날짜 구하기
    public String getTomorrow() {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return AppConstants.dateFormat5.format(cal.getTime());
    }
    //전일 날짜 구하기
    public String getDayBefore(int amount) {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.DAY_OF_MONTH, (amount * -1));
        return AppConstants.dateFormat5.format(cal.getTime());
    }
    //이전달 구하기
    public String getMonthBefore(int amount) {
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.add(Calendar.MONTH, (amount * -1));
        return AppConstants.dateFormat5.format(cal.getTime());
    }

    //프래그먼트 로딩시 자동실행
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    //프래그먼트 종료시 자동실행
    @Override
    public void onDetach() {
        super.onDetach();
        if (context != null) {
            context = null;
        }
    }

}
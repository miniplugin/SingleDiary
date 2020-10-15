package org.techtown.diary;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public abstract class ValueFormatter implements IAxisValueFormatter, IValueFormatter {

    /**
     * <b> 사용하지 마세요 </ b>, 이전 버전과의 호환성을 위해서만 가능하며 향후 버전에서 제거됩니다.
     */
    @Override
    @Deprecated
    public String getFormattedValue(float value, AxisBase axis) {
        return getFormattedValue(value);
    }

    /**
     * <b> 사용하지 마십시오 </ b>. 이전 버전과의 호환성을 위해서만 가능하며 향후 버전에서 제거 될 예정입니다.
     */
    @Override
    @Deprecated
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return getFormattedValue(value);
    }

    /**
     * 레이블을 그릴 때 호출되며 숫자를 형식화 된 문자열로 변경하는 데 사용됩니다.
     */
    public String getFormattedValue(float value) {
        return String.valueOf(value);
    }

    /**
     * 축 라벨을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getAxisLabel(float value, AxisBase axis) {
        return getFormattedValue(value);
    }

    /**
     * 막대 레이블을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getBarLabel(BarEntry barEntry) {
        return getFormattedValue(barEntry.getY());
    }

    /**
     * 누적 막대 레이블을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getBarStackedLabel(float value, BarEntry stackedEntry) {
        return getFormattedValue(value);
    }

    /**
     * 선 및 분산 라벨을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getPointLabel(Entry entry) {
        return getFormattedValue(entry.getY());
    }

    /**
     * 원형 값 라벨을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getPieLabel(float value, PieEntry pieEntry) {
        return getFormattedValue(value);
    }

    /**
     * 레이더 값 라벨을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getRadarLabel(RadarEntry radarEntry) {
        return getFormattedValue(radarEntry.getY());
    }

    /**
     * 거품 크기 라벨을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getBubbleLabel(BubbleEntry bubbleEntry) {
        return getFormattedValue(bubbleEntry.getSize());
    }

    /**
     * 높은 라벨을 그리는 데 사용되며 기본적으로 {@link #getFormattedValue (float)}를 호출합니다.
     */
    public String getCandleLabel(CandleEntry candleEntry) {
        return getFormattedValue(candleEntry.getHigh());
    }

}
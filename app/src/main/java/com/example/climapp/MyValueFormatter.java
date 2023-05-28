package com.example.climapp;

import static com.example.climapp.utils.FormatMinuteToHour.getHourFormatFromAbsoluteMinutes;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyValueFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return getHourFormatFromAbsoluteMinutes(value);
    }
}
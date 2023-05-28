package fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.climapp.MyMarkerView;
import com.example.climapp.MyValueFormatter;
import com.example.climapp.R;

import com.example.climapp.models.Sensors;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SensorsFragment extends Fragment {
    private MyMarkerView mv;

    private ArrayList<Sensors> sensorsArrayList = new ArrayList<>();

    private ArrayList<Entry> temperatures = new ArrayList<>();
    public static final float MINIMUM_TEMPERATURE = -20f;
    public static final float MAXIMUM_TEMPERATURE = 60f;

    private ArrayList<Entry> humidities = new ArrayList<>();
    public static final float MINIMUM_HUMIDITY = 0f;
    public static final float MAXIMUM_HUMIDITY = 100f;

    private ArrayList<Entry> luminosities = new ArrayList<>();
    public static final float MINIMUM_LUMINOSITY = 0f;
    public static final float MAXIMUM_LUMINOSITY = 1020f;

    public static final String FORMAT_DATE_FULL = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_DATE_SHORT = "yyyy-MM-dd";

    public static final float DAY_IN_MINUTES = 1440f;

    public SensorsFragment() {
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mv = new MyMarkerView(context, R.layout.my_maker_view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDataFromDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors, container, false);

        LineChart temperatureChart = view.findViewById(R.id.temperature_chart);
        temperatureChart.setTouchEnabled(true);
        temperatureChart.setPinchZoom(true);
        temperatureChart.setMarker(mv);
        mv.setChartView(temperatureChart);

        createChart(temperatureChart, MINIMUM_TEMPERATURE, MAXIMUM_TEMPERATURE);
        setDataToChart(temperatureChart, temperatures, "temperature");

        LineChart humidityChart = view.findViewById(R.id.humidity_chart);
        humidityChart.setTouchEnabled(true);
        humidityChart.setPinchZoom(true);
        humidityChart.setMarker(mv);
        mv.setChartView(humidityChart);

        createChart(humidityChart, MINIMUM_HUMIDITY, MAXIMUM_HUMIDITY);
        setDataToChart(humidityChart, luminosities, "humidity");

        LineChart luminosityChart = view.findViewById(R.id.luminosity_chart);
        luminosityChart.setTouchEnabled(true);
        luminosityChart.setPinchZoom(true);
        luminosityChart.setMarker(mv);
        mv.setChartView(luminosityChart);

        createChart(luminosityChart, MINIMUM_LUMINOSITY, MAXIMUM_LUMINOSITY);
        setDataToChart(luminosityChart, luminosities, "luminosity");

        return view;
    }

    public void createChart(LineChart chart, float yAxisMinimum, float yAxisMaximum) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(DAY_IN_MINUTES);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new MyValueFormatter());

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(yAxisMinimum);
        yAxis.setAxisMaximum(yAxisMaximum);
        yAxis.setDrawGridLines(false);

        chart.getAxisRight().setEnabled(false);
    }

    private void setDataToChart(LineChart chart, ArrayList<Entry> values, String label) {
        LineDataSet set1;
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, label);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(0f);
            set1.setCircleRadius(2f);

            set1.setValueTextSize(9f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            chart.setData(new LineData(dataSets));
        }
    }

    private void getDataFromDatabase() {
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        String todayFormatted = DateTimeFormatter.ofPattern(FORMAT_DATE_SHORT).format(today);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(todayFormatted);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Sensors> values = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Map<String, Object> value = (Map) ds.getValue();

                        int minute = 0;
                        LocalDateTime minuteInDate = null;
                        try {
                            String date = (String) value.get("date");
                            assert date != null;
                           minuteInDate = LocalDateTime.ofInstant( new SimpleDateFormat(FORMAT_DATE_FULL, Locale.US).parse(date).toInstant(),
                                    ZoneId.systemDefault());

                            assert minuteInDate != null;
                            minute = (int) Duration.between(today, minuteInDate).toMinutes();
                        } catch (ParseException e) {

                        }


                        Sensors formattedValue = new Sensors(minuteInDate, (Long) value.get("temperature"), (Long) value.get("humidity"), (Long) value.get("luminosity"));

                        sensorsArrayList.add(formattedValue);
                        temperatures.add(new Entry(minute, formattedValue.temperature));
                        humidities.add(new Entry(minute, formattedValue.humidity));
                        luminosities.add(new Entry(minute, formattedValue.luminosity));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}

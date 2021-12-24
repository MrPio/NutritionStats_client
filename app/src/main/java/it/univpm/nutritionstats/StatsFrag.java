package it.univpm.nutritionstats;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import it.univpm.nutritionstats.utility.Elements;


public class StatsFrag extends Fragment {

    private       Elements   name;
    private       String     other = null;
    public static JSONObject responseStats;
    public static JSONObject responseFilters;

    private TreeMap<LocalDate, Float> nutrientMap = new TreeMap<>();

    private LineChart  lineChartNutrient         = null;
    private PieChart   pieChartPercentage        = null;
    private TextView   textViewStatTitle         = null;
    private TextView   textViewMean              = null;
    private TextView   textViewVariance          = null;
    private TextView   textViewStandardDeviation = null;
    private ScrollView statsScroll               = null;

    private boolean pieChartPercentageAnimated = false;

    public StatsFrag(Elements name) {
        this.name = name;
    }

    public StatsFrag(String other) {
        this.other = other;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChartNutrient = getView().findViewById(R.id.lineChartNutrient);
        pieChartPercentage = getView().findViewById(R.id.pieChartPercentage);
        textViewStatTitle = getView().findViewById(R.id.textViewStatTitle);
        textViewMean = getView().findViewById(R.id.textViewMean);
        textViewVariance = getView().findViewById(R.id.textViewVariance);
        textViewStandardDeviation = getView().findViewById(R.id.textViewStandardDeviation);
        statsScroll = getView().findViewById(R.id.statsScroll);


        if (other == null) {
        textViewStatTitle.setText(name.name().replace("_", " ") + " graph:");
            JSONObject mean =
                    (JSONObject) ((JSONObject) responseStats.get("MEAN")).get("statsValues");
            float meanValue = 0.0f;
            if (!mean.get(name.name()).toString().equals("NaN"))
                meanValue = ((Number) mean.get(name.name())).floatValue();
            textViewMean.setText(String.format("%.2f", meanValue));
            JSONObject sd =
                    (JSONObject) ((JSONObject) responseStats.get("STANDARD_DEVIATION")).get("statsValues");
            float standardDeviation = 0.0f;
            if (!sd.get(name.name()).toString().equals("NaN"))
                standardDeviation = ((Number) sd.get(name.name())).floatValue();
            textViewStandardDeviation.setText(String.format("%.2f", standardDeviation));
            textViewVariance.setText(String.format("%.2f", (float) Math.pow(standardDeviation, 2)));
        } else {
            textViewStatTitle.setText("CALORIE graph:");
            JSONObject mean =
                    (JSONObject) ((JSONObject) responseStats.get("MEAN"));
            float meanValue = 0.0f;
            if (!mean.get("calories").toString().equals("NaN"))
                meanValue = ((Number) mean.get("calories")).floatValue();
            textViewMean.setText(String.format("%.2f", meanValue));
            JSONObject sd =
                    (JSONObject) ((JSONObject) responseStats.get("STANDARD_DEVIATION"));
            float standardDeviation = 0.0f;
            if (!sd.get("calories").toString().equals("NaN"))
                standardDeviation = ((Number) sd.get("calories")).floatValue();
            textViewStandardDeviation.setText(String.format("%.2f", standardDeviation));
            textViewVariance.setText(String.format("%.2f", (float) Math.pow(standardDeviation, 2)));

            pieChartPercentage.setVisibility(View.GONE);
        }

        loadData();
        loadNutrientChart();
        loadResponseStats();
    }

    private void loadData() {
        if (other == null) {
            nutrientMap = new TreeMap<>();
            JSONObject diary = (JSONObject) responseFilters.get("diary");
            if (!diary.containsKey("dayList"))
                return;
            for (Object day : (JSONArray) (diary.get("dayList"))) {
                JSONObject sumValues = (JSONObject) ((JSONObject) day).get("sumValues");
                String date = (String) ((JSONObject) day).get("date");
                float value = 0.0f;
                if (sumValues.containsKey(name.name()))
                    value = ((Number) sumValues.get(name.name())).floatValue();
                nutrientMap.put(LocalDate.parse(date), value);
            }
        } else {
            nutrientMap = new TreeMap<>();
            JSONObject diary = (JSONObject) responseFilters.get("diary");
            if (!diary.containsKey("dayList"))
                return;
            for (Object day : (JSONArray) (diary.get("dayList"))) {
                float sumValues = ((Number) ((JSONObject) day).get("totalCalories")).floatValue();
                String date = (String) ((JSONObject) day).get("date");
                nutrientMap.put(LocalDate.parse(date), sumValues);
            }
        }
    }

    private void loadNutrientChart() {
        ArrayList<Entry> entriesW = new ArrayList<>();

        for (Map.Entry<LocalDate, Float> entry : nutrientMap.entrySet())
            entriesW.add(new Entry(entry.getKey().toEpochDay(), entry.getValue()));

        LineDataSet dataSetW = new LineDataSet(entriesW, "");
        dataSetW.setColors(new ArrayList<Integer>() {{
            add(Color.CYAN);
        }});
        dataSetW.setValueTextColor(Color.WHITE);
        dataSetW.setValueTextSize(16);
        dataSetW.setLineWidth(3.75f);
        dataSetW.setCircleRadius(8f);
        dataSetW.setCircleHoleRadius(4.8f);
        if(other==null) {
            dataSetW.setColor(Color.WHITE);
            dataSetW.setCircleColor(Color.WHITE);
            dataSetW.setHighLightColor(Color.WHITE);
        }
        else{
            dataSetW.setColor(Color.YELLOW);
            dataSetW.setCircleColor(Color.YELLOW);
            dataSetW.setHighLightColor(Color.YELLOW);
        }
        dataSetW.setDrawValues(false);

        LineData dataW = new LineData(dataSetW);//getData(10,5);
        ((LineDataSet) dataW.getDataSetByIndex(0)).setCircleHoleColor(Color.rgb(137, 230, 81));

        dataW.setDrawValues(true);
        dataW.setValueTextSize(20f);
        dataW.setValueTextColor(Color.WHITE);
        dataW.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataW.setValueTextSize(16);

        lineChartNutrient.setDescription(new Description());
        lineChartNutrient.setData(dataW);
        lineChartNutrient.invalidate();
        lineChartNutrient.setClickable(true);
        lineChartNutrient.setVisibility(View.VISIBLE);
        //lineChartNutrient.getXAxis().setTextColor(Color.WHITE);
        lineChartNutrient.getXAxis().setTextSize(13f);
        lineChartNutrient.getAxisLeft().setTextColor(Color.WHITE);
        lineChartNutrient.getAxisLeft().setTextSize(13f);
        lineChartNutrient.getAxisLeft().setTypeface(Typeface.MONOSPACE);
        lineChartNutrient.getXAxis().setLabelRotationAngle(75);
        lineChartNutrient.getXAxis().setLabelCount(6);
        lineChartNutrient.setScaleYEnabled(false);

        Legend l = lineChartNutrient.getLegend();
        l.setEnabled(false);
    }

    private void loadResponseStats() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        JSONObject statsValues =
                (JSONObject) ((JSONObject) responseStats.get("PERCENTAGE")).get("statsValues");
        float carbohydrates = 0.0f;
        float proteins = 0.0f;
        float lipids = 0.0f;

        if (!statsValues.get("CARBOHYDRATE").toString().equals("NaN"))
            carbohydrates = ((Number) statsValues.get("CARBOHYDRATE")).floatValue();
        if (!statsValues.get("PROTEIN").toString().equals("NaN"))
            proteins = ((Number) statsValues.get("PROTEIN")).floatValue();
        if (!statsValues.get("LIPID").toString().equals("NaN"))
            lipids = ((Number) statsValues.get("LIPID")).floatValue();

        entries.add(new PieEntry(carbohydrates, "Carbohydrates"));
        entries.add(new PieEntry(proteins, "Proteins"));
        entries.add(new PieEntry(lipids, "Lipids"));

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChartPercentage));
        data.setValueTextSize(22f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        pieChartPercentage.setData(data);
        pieChartPercentage.setHoleRadius(0);
        pieChartPercentage.setUsePercentValues(true);
        pieChartPercentage.invalidate();
        pieChartPercentage.setHoleColor(Color.TRANSPARENT);
        pieChartPercentage.setEntryLabelTypeface(Typeface.MONOSPACE);
        pieChartPercentage.setEntryLabelColor(Color.WHITE);
        pieChartPercentage.setEntryLabelTextSize(16f);
        pieChartPercentage.setClickable(true);

        pieChartPercentage.animateY(3200, Easing.EaseInSine);

        Legend l = pieChartPercentage.getLegend();
        l.setEnabled(false);
    }
}
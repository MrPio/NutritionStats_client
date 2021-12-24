package it.univpm.nutritionstats;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.simple.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;

import it.univpm.nutritionstats.activity.MainActivity;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.Elements;

public class Statistics extends AppCompatActivity {

    private TabLayout   tabNutrient    = null;
    private FrameLayout statisticFrame = null;

    private String    startDate = "";
    private String    endDate   = "";
    private boolean[] choosen   = new boolean[Elements.values().length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tabNutrient = findViewById(R.id.tabNutrient);
        statisticFrame = findViewById(R.id.statisticFrame);

        filterNutrients();
        filterEndDate();
        filterStartDate();
    }

    private void filterStartDate() {
        DatePickerDialog.OnDateSetListener datePicker =
                (view2, year, monthOfYear, dayOfMonth) -> {
                    startDate =
                            String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
                };
        DatePickerDialog d = new DatePickerDialog(Statistics.this, datePicker,
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth());
        d.setTitle("Start Date:");
        d.setIcon(getResources().getDrawable(R.drawable.stats));
        d.show();
    }

    private void filterEndDate() {
        DatePickerDialog.OnDateSetListener datePicker =
                (view2, year, monthOfYear, dayOfMonth) -> {
                    endDate =
                            String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
                };
        DatePickerDialog d = new DatePickerDialog(Statistics.this, datePicker,
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth());
        d.setTitle("End Date:");
        d.setIcon(getResources().getDrawable(R.drawable.stats));
        d.show();
    }

    private void filterNutrients() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Statistics.this);
        builder.setTitle("Choose at leas one of the followings:");
        builder.setIcon(getResources().getDrawable(R.drawable.stats));

        builder.setMultiChoiceItems(Arrays.toString(Elements.values()).replaceAll("^.|.$", "").split(", "),
                new boolean[Elements.values().length], (dialogInterface, i, b) -> {
                    choosen[i] = b;
                });
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            boolean allFalse = true;
            for (boolean b2 : choosen)
                if (b2)
                    allFalse = false;
            if (allFalse)
                choosen[0] = true;
            sendRequest();
        });

        builder.show();

    }

    private void sendRequest() {
        ArrayList<Elements> elements = new ArrayList<>();
        for (int j = 0; j < choosen.length; j++)
            if (choosen[j])
                elements.add(Elements.values()[j]);
        StatsFrag.responseStats =
                APICommunication.requestStats(MainActivity.token, startDate, endDate, elements);
        StatsFrag.responseFilters =
                APICommunication.requestFilters(MainActivity.token, startDate, endDate, elements);

        for (Elements el : elements) {
            TabLayout.Tab tab = tabNutrient.newTab();
            tab.setText(el.name().replace("_", " "));
            tab.setIcon(el.getDrawable());
            tabNutrient.addTab(tab);
        }

        tabNutrient.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = new StatsFrag(Elements.valueOf(tab.getText().toString().replace(" ","_")));
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.statisticFrame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        Fragment fragment = new StatsFrag(elements.get(0));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.statisticFrame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
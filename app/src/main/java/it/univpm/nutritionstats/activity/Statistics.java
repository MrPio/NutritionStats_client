package it.univpm.nutritionstats.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.StatsFrag;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.enums.Elements;

public class Statistics extends AppCompatActivity {

    private TabLayout   tabNutrient    = null;
    private FrameLayout statisticFrame = null;

    private String    startDate = "";
    private String    endDate   = "";
    private boolean[] choosen   = new boolean[Elements.values().length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Statistics");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_statistics);

        tabNutrient = findViewById(R.id.tabNutrient);
        statisticFrame = findViewById(R.id.statisticFrame);
        Arrays.fill(choosen, true);

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
                choosen, (dialogInterface, i, b) -> {
                    choosen[i] = b;
                });
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
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

        TabLayout.Tab tab = tabNutrient.newTab();
        tab.setText("CALORIE");
        tab.setIcon(R.drawable.calorie);
        tabNutrient.addTab(tab);

        for (Elements el : elements) {
            tab = tabNutrient.newTab();
            tab.setText(el.name().replace("_", " "));
            tab.setIcon(el.getDrawable());
            tabNutrient.addTab(tab);
        }
        final ColorStateList col=tabNutrient.getTabTextColors();
        tabNutrient.setTabTextColors(Color.WHITE,Color.YELLOW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tabNutrient.setBackgroundColor(Color.rgb(220/255f,210/255f,0f));
        }
        tabNutrient.setTabRippleColor(ColorStateList.valueOf(Color.YELLOW));
        tabNutrient.setSelectedTabIndicatorColor(Color.YELLOW);
        for(int i=0;i<tabNutrient.getTabCount();i++)
            tabNutrient.getTabAt(i).getIcon().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);

        tabNutrient.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText()=="CALORIE"){
                    tabNutrient.setTabTextColors(Color.WHITE,Color.YELLOW);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tabNutrient.setBackgroundColor(Color.rgb(200/255f,195/255f,0f));
                    }
                    tabNutrient.setTabRippleColor(ColorStateList.valueOf(Color.YELLOW));
                    tabNutrient.setSelectedTabIndicatorColor(Color.YELLOW);
                    for(int i=1;i<tabNutrient.getTabCount();i++)
                        tabNutrient.getTabAt(i).getIcon().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                    Fragment fragment = new StatsFrag("CALORIE");
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.statisticFrame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
                else{
                    tabNutrient.setTabTextColors(col);
                        tabNutrient.setBackgroundColor(Color.DKGRAY);
                    tabNutrient.setTabRippleColor(ColorStateList.valueOf(Color.MAGENTA));
                    tabNutrient.setSelectedTabIndicatorColor(Color.MAGENTA);
                    for(int i=1;i<tabNutrient.getTabCount();i++)
                        tabNutrient.getTabAt(i).getIcon().setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_IN);
                    Fragment fragment = new StatsFrag(Elements.valueOf(tab.getText().toString().replace(" ","_")));
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.statisticFrame, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        Fragment fragment = new StatsFrag("CALORIE");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.statisticFrame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
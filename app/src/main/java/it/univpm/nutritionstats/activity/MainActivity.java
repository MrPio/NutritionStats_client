package it.univpm.nutritionstats.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.Statistics;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.Circle;
import it.univpm.nutritionstats.utility.DrinkType;
import it.univpm.nutritionstats.utility.EasingFunctionSine;
import it.univpm.nutritionstats.utility.InputOutputImpl;
import it.univpm.nutritionstats.utility.PopUpMenu;
import it.univpm.nutritionstats.utility.Sound;
import it.univpm.nutritionstats.utility.Utilities;

public class MainActivity extends AppCompatActivity {

    public enum Diet {CLASSIC, PESCATARIAN, VEGETARIAN, VEGAN}

    public enum MealType {
        BREAKFAST(0.20f),
        LUNCH(0.40f),
        SNACK(0.10f),
        DINNER(0.30f);

        float dailyNeed;

        MealType(float dailyNeed) {
            this.dailyNeed = dailyNeed;
        }
    }

    public enum Gender {MALE, FEMALE}

    ;
    final        String TOKEN_PATH                    = "token.dat";
    final static int    REQUEST_CODE_ADD              = 1;
    final static int    REQUEST_CODE_LOGIN            = 2;
    final static int    REQUEST_CODE_ADD_FOOD_BY_EAN  = 3;
    final static int    REQUEST_CODE_ADD_FOOD_BY_NAME = 4;
    final static int    REQUEST_STATISTICS = 4;

    public static ArrayList<JSONObject> resultList    = new ArrayList<JSONObject>();
    public static String                dateForValues = null;

    public static String                    token         = "";
    public static String                    userEmail     = "";
    public static String                    userName      = "";
    public static Diet                      diet          = null;
    public static int                       weight        = 0;
    private       TreeMap<LocalDate, Float> weightMap     = new TreeMap<>();
    public static int                       height        = 0;
    public static Gender                    gender        = null;
    public static Date                      birth         = null;
    public static int                       caloricIntake = 0;


    //Current Values
    public static float carbohydrates = 0;
    public static float proteins      = 0;
    public static float lipids        = 0;
    public static float calories      = 0;
    public static float water         = 0;
    public static float fiber         = 0;
    public static float sodium        = 0;
    public static float calcium       = 0;
    public static float potassium     = 0;
    public static float iron          = 0;
    public static float vitaminA      = 0;
    public static float vitaminC      = 0;

    private ConstraintLayout menuDiary                               = null;
    private ConstraintLayout menuUser                                = null;
    private ImageView        imageViewUserPhoto, imageViewUserPhoto2 = null;
    private PieChart     pieChartCPG         = null;
    private TextView     textViewCalories    = null;
    private TextView     textViewCarboydrats = null;
    private TextView     textViewProteins    = null;
    private TextView     textViewLipids      = null;
    private TextView     textViewFiber       = null;
    private TextView     textViewSodium      = null;
    private TextView     textViewCalcium     = null;
    private TextView     textViewPotassium   = null;
    private TextView     textViewIron        = null;
    private TextView     textViewVitaminA    = null;
    private TextView     textViewVitaminC    = null;
    private ProgressBar  progressBarWater    = null;
    private TextView     textViewWaterCount  = null;
    private PieChart     pieChartWater       = null;
    private LinearLayout breakfastFoodList   = null;
    private LinearLayout lunchFoodList       = null;
    private LinearLayout snackFoodList       = null;
    private LinearLayout dinnerFoodList      = null;

    private ImageView imageViewAddButton    = null;
    private ImageView imageViewAddBreakfast = null;
    private ImageView imageViewAddLunch     = null;
    private ImageView imageViewAddSnack     = null;
    private ImageView imageViewAddDinner    = null;
    private ImageView imageViewAddWater     = null;
    private ImageView imageViewAddRuler     = null;
    private ImageView imageViewAddWeight=null;
    private ImageView imageViewViewStats=null;

    private ImageView imageViewDiary   = null;
    private ImageView imageViewUser    = null;
    private TextView  textViewEmail    = null;
    private TextView  textViewNickname = null;
    private TextView  textViewAge      = null;
    private TextView  textViewWeight   = null;
    private TextView  textViewHeight   = null;
    private ImageView imageViewGender  = null;
    private ImageView imageViewDiet    = null;
    private LineChart lineChartWeight  = null;

    private ScrollView scrollViewMenuDiary = null;
    private ImageView  imageViewCalendar   = null;
    private ImageView  imageViewHome       = null;
    private TextView   textViewTitle       = null;

    private int     actualTab        = 0;
    private boolean addButtonPressed = false;
    Circle circleAddButton = null;
    Circle circleRulerButton = null;
    private int addButtonSelected = -1;
    long startTime;
    private boolean pieChartWaterAnimated = false;
    Point screenSize;
    Point movementStart;
    Point movementEnd;
    private boolean measurePressed = false;
    private int measureSelected = -1;
    private boolean updateWeightValueDisplayed;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.nanoTime();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenSize = new Point(size.x, size.y);

        menuDiary = findViewById(R.id.menuDiary);
        menuUser = findViewById(R.id.menuUser);
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto);
        imageViewUserPhoto2 = findViewById(R.id.imageViewUserPhoto2);
        pieChartCPG = findViewById(R.id.pieChartCPG);
        textViewCarboydrats = findViewById(R.id.textViewCarboydrats);
        textViewProteins = findViewById(R.id.textViewProteins);
        textViewLipids = findViewById(R.id.textViewLipids);
        textViewCalories = findViewById(R.id.textViewCalories);
        textViewFiber = findViewById(R.id.textViewFiber);
        textViewSodium = findViewById(R.id.textViewSodium);
        textViewCalcium = findViewById(R.id.textViewCalcium);
        textViewPotassium = findViewById(R.id.textViewPotassium);
        textViewIron = findViewById(R.id.textViewIron);
        textViewVitaminA = findViewById(R.id.textViewVitaminA);
        textViewVitaminC = findViewById(R.id.textViewVitaminC);
        progressBarWater = findViewById(R.id.progressBarWater);
        textViewWaterCount = findViewById(R.id.textViewWaterCount);
        pieChartWater = findViewById(R.id.pieChartWater);
        breakfastFoodList = findViewById(R.id.BreakfastFoodList);
        lunchFoodList = findViewById(R.id.LunchFoodList);
        snackFoodList = findViewById(R.id.SnackFoodList);
        dinnerFoodList = findViewById(R.id.DinnerFoodList);

        imageViewAddButton = findViewById(R.id.imageViewAddButton);
        imageViewAddBreakfast = findViewById(R.id.imageViewAddBreakfast);
        imageViewAddLunch = findViewById(R.id.imageViewAddLunch);
        imageViewAddSnack = findViewById(R.id.imageViewAddSnack);
        imageViewAddDinner = findViewById(R.id.imageViewAddDinner);
        imageViewAddWater = findViewById(R.id.imageViewAddWater);
        imageViewAddRuler = findViewById(R.id.imageViewAddRuler);
        imageViewViewStats = findViewById(R.id.imageViewViewStats);
        imageViewAddWeight = findViewById(R.id.imageViewAddWeight);
        PopUpMenu[] addButtonMenu = {
                new PopUpMenu(imageViewAddBreakfast, R.drawable.breakfast, R.drawable.breakfast_hover, Sound.Sounds.BIP_5),
                new PopUpMenu(imageViewAddLunch, R.drawable.lunch, R.drawable.lunch_hover, Sound.Sounds.BIP_3),
                new PopUpMenu(imageViewAddSnack, R.drawable.snack, R.drawable.snack_hover, Sound.Sounds.BIP_4),
                new PopUpMenu(imageViewAddDinner, R.drawable.dinner, R.drawable.dinner_hover, Sound.Sounds.BIP_6)
        };
        PopUpMenu[] rulerButtonMenu = {
                new PopUpMenu(imageViewViewStats, R.drawable.stats, R.drawable.stats_hover, Sound.Sounds.BIP_5),
                new PopUpMenu(imageViewAddWeight, R.drawable.weight, R.drawable.weight_hover, Sound.Sounds.BIP_3)
        };

        imageViewDiary = findViewById(R.id.imageViewDiary);
        imageViewUser = findViewById(R.id.imageViewUser);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewNickname = findViewById(R.id.textViewNickname);
        textViewAge = findViewById(R.id.textViewAge);
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewHeight = findViewById(R.id.textViewHeight);
        imageViewGender = findViewById(R.id.imageViewGender);
        imageViewDiet = findViewById(R.id.imageViewDiet);
        lineChartWeight = findViewById(R.id.lineChartWeight);

        scrollViewMenuDiary = findViewById(R.id.scrollViewMenuDiary);
        imageViewCalendar = findViewById(R.id.imageViewCalendar);
        imageViewHome = findViewById(R.id.imageViewHome);
        textViewTitle = findViewById(R.id.textViewTitle);

        if (isLogged()) {
            String savedLogin = new InputOutputImpl(getApplicationContext(), TOKEN_PATH).readFile();
            userName = savedLogin.split(":")[0];
            userEmail = savedLogin.split(":")[1];
            Toast.makeText(getApplicationContext(), "Welcome back " + userName + "!", Toast.LENGTH_SHORT).show();

            fromUserToDiary();
            imageViewDiary.animate().alpha(1f).scaleX(0.75f).scaleY(0.75f).setDuration(400).start();
            imageViewUser.animate().alpha(0.5f).scaleX(0.6f).scaleY(0.6f).setDuration(400).start();
            loadPieChartData();
        } else signUp();

        if (dateForValues != null) {
            imageViewHome.setVisibility(View.VISIBLE);
            textViewTitle.setText(dateForValues.replace("-", "/") + " VALUES:");
        }


        imageViewDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actualTab == 0)
                    return;
                imageViewDiary.animate().alpha(1f).scaleX(0.75f).scaleY(0.75f).setDuration(400).start();
                imageViewUser.animate().alpha(0.5f).scaleX(0.6f).scaleY(0.6f).setDuration(400).start();
                fromUserToDiary();
            }
        });

        imageViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actualTab == 1)
                    return;
                imageViewDiary.animate().alpha(0.5f).scaleX(0.6f).scaleY(0.6f).setDuration(400).start();
                imageViewUser.animate().alpha(1f).scaleX(0.75f).scaleY(0.75f).setDuration(400).start();
                fromDiaryToUser();
            }
        });

        imageViewAddButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float singleStep = 90f / (addButtonMenu.length - 1);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    addButtonSelected = 0;
                    circleAddButton =
                            new Circle(460, new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY()));
                    addButtonPressed = true;
                    imageViewAddButton.animate().scaleX(1.30f).scaleY(1.30f).setDuration(160).start();
                    makeSound(Sound.Sounds.BIP_13);
                    imageViewAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_button_hover));

                    int count =addButtonMenu.length;
                    for (PopUpMenu iv : addButtonMenu) {
                        float angle = 90f+singleStep * --count;
                        iv.getImageView().animate().alpha(1.0f).scaleX(1f).scaleY(1f)
                                .translationX(circleAddButton.getPointFromAngle(angle).x)
                                .translationY(-circleAddButton.getPointFromAngle(angle).y).start();
                    }
                    imageViewAddWater.setAlpha(0f);
                    imageViewAddWater.setVisibility(View.VISIBLE);
                    imageViewAddWater.animate().scaleX(1.35f).scaleY(1.35f).alpha(1.0f).start();
                    imageViewAddButton.animate().alpha(0.0f).start();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    int radius = view.getMeasuredWidth();
                    Point center = new Point(radius, radius);
                    Circle hit = new Circle(center);
                    float distance =
                            hit.getDistaceFromCenter(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));

                    if (distance > 580) {
                        if (addButtonSelected == -1)
                            return false;
                        addButtonSelected = -1;
                        for (PopUpMenu iv : addButtonMenu) {
                            iv.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                            iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                        }
                        imageViewAddWater.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    } else {
                        float angle =
                                circleAddButton.getAngleByPoint(new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY()));
                        if (circleAddButton.getDistaceFromCenter(new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) < 170) {
                            if (addButtonSelected == 0) return true;
                            makeSound(Sound.Sounds.BIP_2);
                            addButtonSelected = 0;
                            for (PopUpMenu iv : addButtonMenu) {
                                iv.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                                iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                            }
                            imageViewAddWater.animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                            return true;
                        }
                        int step = addButtonMenu.length-(int) ((angle - 90+ singleStep/2)/singleStep);
                        if(step<=0 ||step>addButtonMenu.length)
                            return false;
                        if (addButtonSelected == step) return true;
                        addButtonSelected=step;
                        makeSound(addButtonMenu[step-1].getSound());
                        int count = 1;
                        for (PopUpMenu iv : addButtonMenu) {
                            if (count++ == addButtonSelected) {
                                iv.getImageView().animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                                iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawableHover()));
                            } else {
                                iv.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                                iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                            }
                        }
                        imageViewAddWater.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    addButtonPressed = false;
                    imageViewAddButton.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
                    imageViewAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_button));
                    if (addButtonSelected == -1)
                        Sound.makeSound(getApplicationContext(), Sound.Sounds.NO_BUY);
                    else
                        makeSound(Sound.Sounds.BIP_3);

                    for (PopUpMenu iv : addButtonMenu) {
                            iv.getImageView().animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                            iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                    }
                    imageViewAddWater.animate().alpha(0.0f).start();
                    imageViewAddButton.animate().alpha(1.0f).start();

                    if (addButtonSelected > 0) {
                        AddFood.mealType = MealType.values()[addButtonSelected - 1];
                        Intent i = (new Intent(MainActivity.this, AddFood.class));
                        startActivityForResult(i, REQUEST_CODE_ADD);
                    } else if (addButtonSelected == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Please the amount of water you drank [mL]:");
                        final Spinner input = new Spinner(getApplicationContext());
                        input.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.spinner_drink_type, DrinkType.values()));
                        input.setPadding(50, 50, 0, 0);
                        builder.setView(input);
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            new Thread(() -> {
                                APICommunication.requestAddWater(MainActivity.token, (int) ((DrinkType) input.getSelectedItem()).getValue());
                            }).start();

                            Toast.makeText(MainActivity.this, "Water successfully added!", Toast.LENGTH_SHORT).show();
                            makeSound(Sound.Sounds.WATER_SPLASH);

                            EasingFunctionSine.delay = water / 2000f;
                            water += (int) ((DrinkType) input.getSelectedItem()).getValue();
                            loadWaterChart();
                            pieChartWater.animateY(1000, EasingFunctionSine.EaseOutSineDelay);
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                }
                return true;
            }
        });

        APICommunication.requestFoodList(token);

        scrollViewMenuDiary.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!pieChartWaterAnimated && scrollViewMenuDiary.getScrollY() > 630) {
                    pieChartWater.animateY(3200, Easing.EaseInSine);
                    pieChartWaterAnimated = true;
                }
                // DO SOMETHING WITH THE SCROLL COORDINATES
            }
        });

        imageViewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        String dayId = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        String todayDayId = LocalDate.now().getDayOfMonth() + "-" +
                                LocalDate.now().getMonthValue() + "-" + LocalDate.now().getYear();

                        if (dayId.equals(todayDayId)) {
                            imageViewHome.performClick();
                            return;
                        }
                        dateForValues = dayId;
                        imageViewHome.setVisibility(View.VISIBLE);

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                };
                if (dateForValues == null) {
                    new DatePickerDialog(MainActivity.this, date,
                            LocalDate.now().getYear(),
                            LocalDate.now().getMonthValue() - 1,
                            LocalDate.now().getDayOfMonth()).show();
                } else
                    new DatePickerDialog(MainActivity.this, date,
                            Integer.parseInt(dateForValues.split("-")[2]),
                            Integer.parseInt(dateForValues.split("-")[1]) - 1,
                            Integer.parseInt(dateForValues.split("-")[0])).show();

            }
        });

        imageViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateForValues = null;

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        imageViewAddRuler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float baseAngle=90f;
                if(rulerButtonMenu.length==2)
                    baseAngle=105f;
                float singleStep =( 90f-(baseAngle-90f)*2f) / (rulerButtonMenu.length - 1);
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    measureSelected = 0;
                    circleRulerButton =
                            new Circle(460, new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY()));
                    measurePressed = true;
                    view.animate().scaleX(1.30f).scaleY(1.30f).setDuration(160).start();
                    makeSound(Sound.Sounds.BIP_13);
                    ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.ruler_hover));

                    int count =rulerButtonMenu.length;
                    for (PopUpMenu iv : rulerButtonMenu) {
                        float angle = baseAngle+singleStep * --count;
                        iv.getImageView().animate().alpha(1.0f).scaleX(1f).scaleY(1f)
                                .translationX(circleRulerButton.getPointFromAngle(angle).x)
                                .translationY(-circleRulerButton.getPointFromAngle(angle).y).start();
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    int radius = view.getMeasuredWidth();
                    Point center = new Point(radius, radius);
                    Circle hit = new Circle(center);
                    float distance =
                            hit.getDistaceFromCenter(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));

                    if (distance > 580) {
                        if (measureSelected == -1)
                            return false;
                        measureSelected = -1;
                        for (PopUpMenu iv : rulerButtonMenu) {
                            iv.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                            iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                        }
                        view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    } else {
                        float angle =
                                circleRulerButton.getAngleByPoint(new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY()));
                        if (circleRulerButton.getDistaceFromCenter(new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) < 170) {
                            if (measureSelected == 0) return true;
                            makeSound(Sound.Sounds.BIP_2);
                            measureSelected = 0;
                            for (PopUpMenu iv : rulerButtonMenu) {
                                iv.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                                iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                            }
                            view.animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                            return true;
                        }
                        int step = rulerButtonMenu.length-(int) ((angle - baseAngle+ singleStep/2)/singleStep);
                        if(step<=0 ||step>rulerButtonMenu.length)
                            return false;
                        if (measureSelected == step)
                            return true;
                        measureSelected=step;
                        makeSound(rulerButtonMenu[step-1].getSound());
                        int count = 1;
                        for (PopUpMenu iv : rulerButtonMenu) {
                            if (count++ == measureSelected) {
                                iv.getImageView().animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                                iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawableHover()));
                            } else {
                                iv.getImageView().animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                                iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                            }
                        }
                        view.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    addButtonPressed = false;
                    view.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
                    ((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.ruler));
                    if (measureSelected == -1)
                        Sound.makeSound(getApplicationContext(), Sound.Sounds.NO_BUY);
                    else
                        makeSound(Sound.Sounds.BIP_3);

                    for (PopUpMenu iv : rulerButtonMenu) {
                        iv.getImageView().animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                        iv.getImageView().setImageDrawable(getResources().getDrawable(iv.getDrawable()));
                    }
                    view.animate().alpha(0.0f).start();
                    view.animate().alpha(1.0f).start();

                    if (measureSelected == 1) {
                        Intent i = (new Intent(MainActivity.this, Statistics.class));
                        startActivityForResult(i, REQUEST_STATISTICS);
                    }
                    else if (measureSelected == 2)
                        updateWeightValue(weightMap.lastKey(), weightMap.lastEntry().getValue());

                }







/*                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    measureSelected = 0;
                    imageViewAddRuler.animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                    Sound.makeSound(getApplicationContext(), Sound.Sounds.BIP_13);
                    imageViewAddRuler.setImageDrawable(getResources().getDrawable(R.drawable.ruler_hover));
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    int radius = view.getMeasuredWidth();
                    Point center = new Point(radius, radius);
                    Circle hit = new Circle(center);
                    float distance =
                            hit.getDistaceFromCenter(new Point((int) motionEvent.getX(), (int) motionEvent.getY()));

                    if (measureSelected && distance > radius) {
                        measureSelected = false;
                        imageViewAddRuler.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        imageViewAddRuler.setImageDrawable(getResources().getDrawable(R.drawable.ruler));
                    } else if (!measureSelected && distance < radius) {
                        measureSelected = true;
                        imageViewAddRuler.animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        Sound.makeSound(getApplicationContext(), Sound.Sounds.BIP_13);
                        imageViewAddRuler.setImageDrawable(getResources().getDrawable(R.drawable.ruler_hover));
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    imageViewAddRuler.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    imageViewAddRuler.setImageDrawable(getResources().getDrawable(R.drawable.ruler));
                    if (measureSelected) {
                        measureSelected = false;
                        updateWeightValue(weightMap.lastKey(), weightMap.lastEntry().getValue());
                    }
                }*/
                return true;
            }
        });

        lineChartWeight.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (!updateWeightValueDisplayed)
                    updateWeightValue(LocalDate.ofEpochDay((long) e.getX()), e.getY());
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void loadPieChartData() {
        loadTodayValues();

        loadCPGChart();
        loadWaterChart();
        loadWeightChart();
    }

    private void loadCPGChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((carbohydrates * 4f) / calories, "Carbohydrates"));
        entries.add(new PieEntry((proteins * 4f) / calories, "Proteins"));
        entries.add(new PieEntry((lipids * 9f) / calories, "Lipids"));

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
        data.setValueFormatter(new PercentFormatter(pieChartCPG));
        data.setValueTextSize(22f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        pieChartCPG.setData(data);
        pieChartCPG.setHoleRadius(0);
        pieChartCPG.setUsePercentValues(true);
        pieChartCPG.invalidate();
        pieChartCPG.setHoleColor(Color.TRANSPARENT);
        pieChartCPG.setEntryLabelTypeface(Typeface.MONOSPACE);
        pieChartCPG.setEntryLabelColor(Color.WHITE);
        pieChartCPG.setEntryLabelTextSize(16f);
        pieChartCPG.setClickable(true);

        pieChartCPG.animateY(3200, Easing.EaseInOutSine);

        Legend l = pieChartCPG.getLegend();
        l.setEnabled(false);
    }

    private void loadWaterChart() {
        float progress = water / 2000;
        if (progress > 1f)
            progress = 1f;
        progressBarWater.setMax(100);
        progressBarWater.setProgress((int) (progress * 100));
        int color =
                Color.rgb((3 + (progress - 0.5f) * 2 * 15) / 255f, 244f / 255, (244 - 240 * (progress - 0.5f) * 2) / 255f);
        if (progress < 0.5f)
            color = Color.rgb(3, 120 + 124 * progress * 2, 244);
        else
            color =
                    Color.rgb((3 + (progress - 0.5f) * 2 * 15) / 255f, 244f / 255, (244 - 240 * (progress - 0.5f) * 2) / 255f);

        progressBarWater.setProgressTintList(ColorStateList.valueOf(color));
        textViewWaterCount.setText(String.valueOf((int) water) + "ml / 2000ml");

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(progress, "Water"));
        entries.add(new PieEntry(1 - progress, ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        int finalColor = color;
        dataSet.setColors(new ArrayList<Integer>() {{
            add(finalColor);
            add(Color.TRANSPARENT);
        }});
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        data.setValueFormatter(new PercentFormatter(pieChartWater));
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        pieChartWater.setData(data);
        pieChartWater.setHoleRadius(0f);
        pieChartWater.setUsePercentValues(true);
        pieChartWater.invalidate();
        pieChartWater.setHoleColor(Color.TRANSPARENT);
        pieChartWater.setEntryLabelTypeface(Typeface.MONOSPACE);
        pieChartWater.setEntryLabelColor(Color.GRAY);
        pieChartWater.setEntryLabelTextSize(20f);
        pieChartWater.setClickable(true);
        pieChartWater.setVisibility(View.VISIBLE);

        Legend l = pieChartWater.getLegend();
        l.setEnabled(false);
    }

    private void loadWeightChart() {
        ArrayList<Entry> entriesW = new ArrayList<>();
        int count = 0;
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        for (Map.Entry<LocalDate, Float> entry : weightMap.entrySet())
            //TODO axis x
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
        dataSetW.setColor(Color.WHITE);
        dataSetW.setCircleColor(Color.WHITE);
        dataSetW.setHighLightColor(Color.WHITE);
        dataSetW.setDrawValues(false);

        LineData dataW = new LineData(dataSetW);//getData(10,5);
        ((LineDataSet) dataW.getDataSetByIndex(0)).setCircleHoleColor(Color.rgb(137, 230, 81));

        dataW.setDrawValues(true);
        dataW.setValueTextSize(20f);
        dataW.setValueTextColor(Color.WHITE);
        dataW.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataW.setValueTextSize(16);

        lineChartWeight.setDescription(new Description());
        lineChartWeight.setData(dataW);
        lineChartWeight.invalidate();
        lineChartWeight.setClickable(true);
        lineChartWeight.setVisibility(View.VISIBLE);
        //lineChartWeight.getXAxis().setTextColor(Color.WHITE);
        lineChartWeight.getXAxis().setTextSize(13f);
        lineChartWeight.getAxisLeft().setTextColor(Color.WHITE);
        lineChartWeight.getAxisLeft().setTextSize(18f);
        lineChartWeight.getAxisLeft().setTypeface(Typeface.MONOSPACE);
        lineChartWeight.getXAxis().setLabelRotationAngle(75);
        lineChartWeight.getXAxis().setLabelCount(6);
        lineChartWeight.setScaleYEnabled(false);

        Legend l = lineChartWeight.getLegend();
        l.setEnabled(true);
    }

    private void updateWeightValue(LocalDate defaultDate, float defaultValue) {
        updateWeightValueDisplayed = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Update your weight value [KG]:");
        builder.setIcon(getResources().getDrawable(R.drawable.ruler_anim));

        final LinearLayout menu = new LinearLayout(getApplicationContext());
        menu.setOrientation(LinearLayout.VERTICAL);
        final TextView dateText = new TextView(getApplicationContext());
        dateText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        dateText.setPadding(150, 100, 0, 0);
        dateText.setTextColor(Color.WHITE);
        dateText.setTypeface(Typeface.SERIF);
        dateText.setText("Date:");
        dateText.setTextSize(18f);
        final EditText date = new EditText(getApplicationContext());
        date.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        date.setPadding(150, 80, 0, 0);
        date.setTextColor(Color.WHITE);
        date.setTypeface(Typeface.MONOSPACE);
        date.setTextSize(18f);
            date.setText(defaultDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        final TextView weightText = new TextView(getApplicationContext());
        weightText.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
        weightText.setPadding(150, 100, 0, 0);
        weightText.setTextColor(Color.WHITE);
        weightText.setTypeface(Typeface.SERIF);
        weightText.setText("Weight Value:");
        weightText.setTextSize(18f);
        final EditText weight = new EditText(getApplicationContext());
        weight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        weight.setPadding(150, 80, 0, 0);
        weight.setTextColor(Color.WHITE);
        weight.setTextSize(22f);
        weight.setText(String.valueOf(defaultValue));
        weight.setTextColor(Color.GREEN);
        weight.setTypeface(Typeface.DEFAULT_BOLD);

        weight.setOnKeyListener((view1, i, keyEvent) -> {
            weight.setTextColor(Color.WHITE);
            weight.setTypeface(Typeface.DEFAULT);
            return false;
        });

        weight.setOnFocusChangeListener(((view, b) -> {
            if (!b) return;

            final Dialog d = new Dialog(MainActivity.this);
            d.setTitle("Weight value:");
            d.setContentView(R.layout.float_input_dialog);
            Button b1 = (Button) d.findViewById(R.id.button1);
            Button b2 = (Button) d.findViewById(R.id.button2);
            final NumberPicker numberPickerDecimal =
                    (NumberPicker) d.findViewById(R.id.numberPickerDecimal);
            numberPickerDecimal.setMaxValue(300);
            numberPickerDecimal.setMinValue(10);
            numberPickerDecimal.setWrapSelectorWheel(false);
            //numberPickerDecimal.setTextSize(84);
            numberPickerDecimal.setValue((int) defaultValue);
            final NumberPicker numberPickerSigned =
                    (NumberPicker) d.findViewById(R.id.numberPickerSigned);
            numberPickerSigned.setMaxValue(9);
            numberPickerSigned.setMinValue(0);
            numberPickerSigned.setWrapSelectorWheel(false);
            //numberPickerSigned.setTextSize(84);
            try {
                numberPickerSigned.setValue(Integer.parseInt(String.valueOf(defaultValue).split("\\.")[1]));
            } catch (Exception e) {
            }
            ;

            b1.setOnClickListener(v -> {
                if (!weight.getText().toString().equals(numberPickerDecimal.getValue() + "." + numberPickerSigned.getValue())) {
                    weight.setTextColor(Color.WHITE);
                    weight.setTypeface(Typeface.DEFAULT);
                }
                weight.setText(numberPickerDecimal.getValue() + "." + numberPickerSigned.getValue());
                d.dismiss();
            });
            b2.setOnClickListener(v -> d.dismiss());
            d.show();
        }));

        date.setOnFocusChangeListener((view1, b) -> {
            if (!b) return;
            DatePickerDialog.OnDateSetListener datePicker =
                    (view2, year, monthOfYear, dayOfMonth) -> {
                        String dayId =
                                String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear + 1)) + "/" + year;
                        date.setText(dayId);
                        final DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        final LocalDate localDate = LocalDate.parse(dayId, formatter);
                        if (weightMap.containsKey(localDate)) {
                            weight.setText(weightMap.get(localDate).toString());
                            weight.setTextColor(Color.GREEN);
                            weight.setTypeface(Typeface.DEFAULT_BOLD);
                        }
                    };
            if (date.getText().toString().isEmpty()) {
                new DatePickerDialog(MainActivity.this, datePicker,
                        LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue() - 1,
                        LocalDate.now().getDayOfMonth()).show();
            } else
                new DatePickerDialog(MainActivity.this, datePicker,
                        Integer.parseInt(date.getText().toString().split("/")[2]),
                        Integer.parseInt(date.getText().toString().split("/")[1]) - 1,
                        Integer.parseInt(date.getText().toString().split("/")[0])).show();
        });

        menu.addView(dateText);
        menu.addView(date);
        menu.addView(weightText);
        menu.addView(weight);
        builder.setView(menu);
        builder.setPositiveButton("OK", (dialog, which) -> {
            new Thread(() -> {
                if (!date.getText().toString().isEmpty() && !weight.getText().toString().isEmpty()) {
                    APICommunication.requestUpdateWeight(MainActivity.token, date.getText().toString(),
                            Float.valueOf(weight.getText().toString()));
                }
            }).start();

            Toast.makeText(MainActivity.this, "Weight data updated successfully!", Toast.LENGTH_SHORT).show();

            weightMap.put(LocalDate.parse(date.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    Float.valueOf(weight.getText().toString()));
            loadWeightChart();
            lineChartWeight.animateX(1500, Easing.EaseInSine);
            updateWeightValueDisplayed = false;
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            updateWeightValueDisplayed = false;
        });
        builder.setOnCancelListener((dialogInterface) -> updateWeightValueDisplayed = false);

        builder.show();
    }

    private void loadTodayValues() {
        pieChartWater.setVisibility(View.INVISIBLE);
        breakfastFoodList.removeAllViews();
        lunchFoodList.removeAllViews();
        snackFoodList.removeAllViews();
        dinnerFoodList.removeAllViews();
        carbohydrates = 0f;
        proteins = 0f;
        lipids = 0f;
        calories = 0f;
        water = 0f;

        JSONObject response = APICommunication.requestTodayValues(token);
        if (response.keySet().contains("result") && response.get("result").toString().contains("error")) {
            Toast.makeText(getApplicationContext(), response.get("result").toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.keySet().contains("result") && response.get("result").equals("nothing to show on this day..."))
            return;
        carbohydrates = ((Number) response.get("carbohydrate")).floatValue();
        proteins = ((Number) response.get("protein")).floatValue();
        lipids = ((Number) response.get("lipid")).floatValue();
        calories = ((Number) response.get("calories")).floatValue();
        water =
                ((Number) response.get("water")).floatValue() + ((Number) response.get("waterfromfood")).floatValue();
        fiber = ((Number) response.get("fiber")).floatValue() / 1000f;
        calcium = ((Number) response.get("calcium")).floatValue();
        sodium = ((Number) response.get("sodium")).floatValue();
        potassium = ((Number) response.get("potassium")).floatValue();
        iron = ((Number) response.get("iron")).floatValue();
        vitaminA = ((Number) response.get("vitamina")).floatValue();
        vitaminC = ((Number) response.get("vitaminc")).floatValue();

        textViewCalories.setText("(" + String.valueOf((int) calories) + " kcal)");
        textViewCarboydrats.setText("(" + String.format("%.2f", carbohydrates) + " gr)");
        textViewProteins.setText("(" + String.format("%.2f", proteins) + " gr)");
        textViewLipids.setText("(" + String.format("%.2f", lipids) + " gr)");
        textViewFiber.setText("(" + String.format("%.3f", fiber) + " gr)");
        textViewSodium.setText("(" + String.format("%.2f", sodium * 1000) + " mg)");
        textViewCalcium.setText("(" + String.format("%.2f", calcium * 1000) + " mg)");
        textViewPotassium.setText("(" + String.format("%.2f", potassium * 1000) + " mg)");
        textViewIron.setText("(" + String.format("%.2f", iron * 1000) + " mg)");
        textViewVitaminA.setText("(" + String.format("%.2f", vitaminA * 1000) + " mg)");
        textViewVitaminC.setText("(" + String.format("%.2f", vitaminC * 1000) + " mg)");

        HashMap<MainActivity.MealType, ArrayList<String>> foodList =
                APICommunication.requestFoodList(token);
        for (Map.Entry<?, ?> entry : foodList.entrySet()) {
            for (String name : (ArrayList<String>) entry.getValue()) {
                TextView food = new TextView(getApplicationContext());
                food.setText(name);
                food.setTextColor(Color.WHITE);
                food.setTextSize(18);
                food.setTypeface(Typeface.MONOSPACE);
                LinearLayout parent = null;
                switch ((MainActivity.MealType) entry.getKey()) {
                    case BREAKFAST:
                        parent = breakfastFoodList;
                        break;
                    case LUNCH:
                        parent = lunchFoodList;
                        break;
                    case SNACK:
                        parent = snackFoodList;
                        break;
                    case DINNER:
                        parent = dinnerFoodList;
                        break;
                }
                parent.addView(food);
            }
        }
    }

    private void fromUserToDiary() {
        makeSound(Sound.Sounds.SLIDE_OUT_2);

        menuUser.setVisibility(View.VISIBLE);
        menuUser.setTranslationX(0);
        menuUser.setAlpha(1);
        menuUser.animate().translationX(screenSize.x).alpha(0).setDuration(401).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (animation.getDuration() == 401)
                    menuUser.setVisibility(View.GONE);
            }
        }).start();

        menuDiary.setVisibility(View.VISIBLE);
        menuDiary.setTranslationX(-screenSize.x);
        menuDiary.setAlpha(0);
        menuDiary.animate().translationX(0).alpha(1).setDuration(400).start();
        actualTab = 0;
    }

    private void fromDiaryToUser() {
        makeSound(Sound.Sounds.SLIDE_OUT_2);

        menuUser.setVisibility(View.VISIBLE);
        menuUser.setTranslationX(screenSize.x);
        menuUser.setAlpha(0);
        menuUser.animate().translationX(0).alpha(1).setDuration(400).start();

        menuDiary.setVisibility(View.VISIBLE);
        menuDiary.setTranslationX(0);
        menuDiary.setAlpha(1);
        menuDiary.animate().translationX(-screenSize.x).alpha(0).setDuration(401).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (animation.getDuration() == 401)
                    menuDiary.setVisibility(View.GONE);
            }
        }).start();
        actualTab = 1;
        lineChartWeight.animateX(1000);
    }

    private boolean isLogged() {
        InputOutputImpl file = new InputOutputImpl(getApplicationContext(), TOKEN_PATH);
        if (!file.existFile())
            return false;
        token = file.readFile().split(":")[2];
        JSONObject response = APICommunication.requestLogin(token);
        //API IRRAGGIUNGIBILE
        if (response.keySet().contains("result") && response.get("result").equals("error: request timeout")) {
            Toast.makeText(getApplicationContext(), "Cannot reach the server! [TIMEOUT ERROR]", Toast.LENGTH_LONG).show();
            finish();
            System.exit(0);
            return false;
        }
        //ERRORE GENERICO NELLA RICHIESTA HTTP
        if (response.get("result").equals("error")) {
            Toast.makeText(getApplicationContext(), response.get("result").toString(), Toast.LENGTH_SHORT).show();
            return true;
        }
        //LOGIN FALLITO
        else if (response.get("result").equals("not found"))
            return false;
        else {
            login(response);
            return true;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void login(JSONObject response) {
        response = (JSONObject) response.get("user");
        textViewEmail.setText(response.get("email").toString());
        textViewNickname.setText(response.get("nickname").toString());
        try {
            birth =
                    new SimpleDateFormat("yyyy-MM-dd").parse(response.get("yearOfBirth").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar dob = Calendar.getInstance();
        dob.set(birth.getYear(), birth.getMonth(), birth.getDay());
        int age = Calendar.getInstance().get(Calendar.YEAR) - birth.getYear() - 1900;
        if (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--;
        textViewAge.setText(String.valueOf(age));
        //textViewAge.setText();
        weightMap = new TreeMap<>();
        for (Object obj : ((JSONObject) response.get("weight")).entrySet())
            weightMap.put(LocalDate.parse(((Map.Entry<String, Number>) obj).getKey()), ((Map.Entry<String, Number>) obj).getValue().floatValue());
        Object[] weights = weightMap.values().toArray();
        textViewWeight.setText(weights[weights.length - 1].toString());
        textViewHeight.setText(response.get("height").toString());
        switch (Diet.valueOf(response.get("diet").toString())) {
            case CLASSIC:
                imageViewDiet.setImageDrawable(getResources().getDrawable(R.drawable.meat));
                break;
            case PESCATARIAN:
                imageViewDiet.setImageDrawable(getResources().getDrawable(R.drawable.fish));
                break;
            case VEGETARIAN:
                imageViewDiet.setImageDrawable(getResources().getDrawable(R.drawable.cheese));
                break;
            case VEGAN:
                imageViewDiet.setImageDrawable(getResources().getDrawable(R.drawable.plant));
                break;
        }
        switch (Gender.valueOf(response.get("gender").toString())) {
            case MALE:
                imageViewGender.setImageDrawable(getResources().getDrawable(R.drawable.male));
                break;
            case FEMALE:
                imageViewGender.setImageDrawable(getResources().getDrawable(R.drawable.female));
                break;
        }
        caloricIntake = ((Number) response.get("dailyCaloricIntake")).intValue();

        String photoUri = new InputOutputImpl(getApplicationContext(), "user_image").readFile();
        Picasso.get().load(photoUri).into(imageViewUserPhoto);
        Picasso.get().load(photoUri).into(imageViewUserPhoto2);
        Log.e("MY", "login" + String.valueOf((System.nanoTime() - startTime) / 1000000f));

    }

    private void signUp() {
        Toast.makeText(getApplicationContext(), "Welcome, please proceed with the signup!", Toast.LENGTH_LONG).show();

        Intent i = (new Intent(MainActivity.this, Login.class));
        startActivityForResult(i, REQUEST_CODE_LOGIN);
    }

    void makeSound(Sound.Sounds sound) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final MediaPlayer[] mPlayer =
                        {MediaPlayer.create(getApplicationContext(), sound.res)};
                mPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mPlayer[0].release();
                        mPlayer[0] = null;
                    }
                });
                mPlayer[0].start();

            }
        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CODE_ADD:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;

            case REQUEST_CODE_LOGIN:
                JSONObject response =
                        new APICommunication().requestSignUp(userName, userEmail, birth, diet, weight, height, gender);
                if (response.get("result").toString().contains("error")) {
                    Toast.makeText(getApplicationContext(), response.get("result").toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                token = response.get("token").toString();
                String formatted =
                        userName + ":" + userEmail + ":" + token;
                new InputOutputImpl(getApplicationContext(), TOKEN_PATH).writeFile(formatted);
                Toast.makeText(getApplicationContext(), "You successfully signed-up!", Toast.LENGTH_SHORT).show();
                fromUserToDiary();
                loadPieChartData();
                break;

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getY() / screenSize.y > 0.7f)
            return super.dispatchTouchEvent(event);
        if (addButtonPressed)
            return super.dispatchTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            movementStart = new Point((int) event.getX(), (int) event.getY());
            if (actualTab == 0) {
                menuUser.setVisibility(View.VISIBLE);
                //menuUser.setTranslationX(screenSize.x);
                // menuUser.setAlpha(0);
            }
            if (actualTab == 1) {
                menuDiary.setVisibility(View.VISIBLE);
                //menuDiary.setTranslationX(-screenSize.x);
                //menuDiary.setAlpha(0);
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (movementStart == null)
                return super.dispatchTouchEvent(event);
            if (actualTab == 0 && (float) movementStart.x / screenSize.x > 0.85) {
                menuUser.setTranslationX(event.getX());
                menuUser.setAlpha(1 - (event.getX() / screenSize.x));
                menuDiary.setVisibility(View.VISIBLE);
                menuUser.setVisibility(View.VISIBLE);
                menuDiary.setTranslationX(-(screenSize.x - event.getX()));
                menuDiary.setAlpha((event.getX() / screenSize.x));
            } else if (actualTab == 1 && (float) movementStart.x / screenSize.x < 0.15) {

                menuUser.setTranslationX(event.getX());
                menuUser.setAlpha(1 - ((float) event.getX() / screenSize.x));
                menuDiary.setVisibility(View.VISIBLE);
                menuUser.setVisibility(View.VISIBLE);
                menuDiary.setTranslationX(event.getX() - screenSize.x);
                menuDiary.setAlpha(((float) event.getX() / screenSize.x));
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (movementStart == null)
                return true;
            movementEnd = new Point((int) event.getX(), (int) event.getY());

            if (actualTab == 0) {
                if ((float) movementStart.x / screenSize.x > 0.85 &&
                        Math.abs(movementStart.x - movementEnd.x) / (float) screenSize.x > 0.28) {
                    makeSound(Sound.Sounds.SLIDE_IN);

                    menuUser.animate().translationX(0).alpha(1).setDuration(300).start();
                    menuDiary.animate().translationX(-screenSize.x).alpha(0).setDuration(304).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (animation.getDuration() == 304)
                                menuDiary.setVisibility(View.GONE);
                        }
                    }).start();
                    actualTab = 1;
                    lineChartWeight.animateX(1000);
                    imageViewDiary.animate().alpha(0.5f).scaleX(0.6f).scaleY(0.6f).setDuration(400).start();
                    imageViewUser.animate().alpha(1f).scaleX(0.75f).scaleY(0.75f).setDuration(400).start();

                } else {
                    menuUser.animate().translationX(screenSize.x).alpha(0).setDuration(301).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (animation.getDuration() == 301)
                                menuUser.setVisibility(View.GONE);
                        }
                    }).start();
                    menuDiary.animate().translationX(0).alpha(1).setDuration(300).start();
                }
            } else if (actualTab == 1) {
                if ((float) movementStart.x / screenSize.x < 0.15 &&
                        Math.abs(movementStart.x - movementEnd.x) / (float) screenSize.x > 0.28) {
                    makeSound(Sound.Sounds.SLIDE_IN);

                    menuUser.animate().translationX(screenSize.x).alpha(0).setDuration(302).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (animation.getDuration() == 302)
                                menuUser.setVisibility(View.GONE);
                        }
                    }).start();
                    menuDiary.animate().translationX(0).alpha(1).setDuration(300).start();
                    actualTab = 0;
                    imageViewDiary.animate().alpha(1f).scaleX(0.75f).scaleY(0.75f).setDuration(400).start();
                    imageViewUser.animate().alpha(0.5f).scaleX(0.6f).scaleY(0.6f).setDuration(400).start();
                } else {
                    menuUser.animate().translationX(0).alpha(1).setDuration(300).start();
                    menuDiary.animate().translationX(-screenSize.x).alpha(0).setDuration(303).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (animation.getDuration() == 303)
                                menuDiary.setVisibility(View.GONE);
                        }
                    }).start();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
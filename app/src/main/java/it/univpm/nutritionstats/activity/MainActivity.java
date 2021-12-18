package it.univpm.nutritionstats.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;
import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.Circle;
import it.univpm.nutritionstats.utility.InputOutputImpl;
import it.univpm.nutritionstats.utility.Sound;

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

    public static ArrayList<JSONObject> resultList    = new ArrayList<JSONObject>();
    public static String                dateForValues = null;

    public static String token         = "";
    public static String userEmail     = "";
    public static String userName      = "";
    public static Diet   diet          = null;
    public static int    weight        = 0;
    public static int    height        = 0;
    public static Gender gender        = null;
    public static Date   birth         = null;
    //Current Values
    public static float  carbohydrates = 0;
    public static float  proteins      = 0;
    public static float  lipids        = 0;
    public static float  calories      = 0;
    public static float  water         = 0;
    public static float  fiber         = 0;
    public static float  sodium        = 0;
    public static float  calcium       = 0;
    public static float  potassium     = 0;
    public static float  iron          = 0;
    public static float  vitaminA      = 0;
    public static float  vitaminC      = 0;

    MediaPlayer mPlayer = new MediaPlayer();
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

    private ImageView imageViewDiary   = null;
    private ImageView imageViewUser    = null;
    private TextView  textViewEmail    = null;
    private TextView  textViewNickname = null;
    private TextView  textViewAge      = null;
    private TextView  textViewWeight   = null;
    private TextView  textViewHeight   = null;
    private ImageView imageViewGender  = null;
    private ImageView imageViewDiet    = null;

    private ScrollView scrollViewMenuDiary = null;
    private ImageView  imageViewCalendar   = null;
    private ImageView  imageViewHome       = null;
    private TextView textViewTitle=null;

    private int     actualTab        = 0;
    private boolean addButtonPressed = false;
    Circle circleAddButton = null;
    private int addButtonSelected = 0;
    long startTime;
    private boolean pieChartWaterAnimated = false;
    Point screenSize;
    Point movementStart;
    Point movementEnd;


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
        ImageView[] addMealList =
                {imageViewAddBreakfast, imageViewAddLunch, imageViewAddSnack, imageViewAddDinner};

        imageViewDiary = findViewById(R.id.imageViewDiary);
        imageViewUser = findViewById(R.id.imageViewUser);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewNickname = findViewById(R.id.textViewNickname);
        textViewAge = findViewById(R.id.textViewAge);
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewHeight = findViewById(R.id.textViewHeight);
        imageViewGender = findViewById(R.id.imageViewGender);
        imageViewDiet = findViewById(R.id.imageViewDiet);
        scrollViewMenuDiary = findViewById(R.id.scrollViewMenuDiary);
        imageViewCalendar = findViewById(R.id.imageViewCalendar);
        imageViewHome = findViewById(R.id.imageViewHome);
        textViewTitle=findViewById(R.id.textViewTitle);

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
            textViewTitle.setText(dateForValues.replace("-","/")+" VALUES:");
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

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    addButtonSelected = 0;
                    circleAddButton =
                            new Circle(460, new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY()));
                    addButtonPressed = true;
                    imageViewAddButton.animate().scaleX(1.30f).scaleY(1.30f).setDuration(160).start();
                    makeSound(Sound.Sounds.BIP_13);
                    imageViewAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_button_hover));


                    addMealList[0].animate().alpha(1.0f).scaleX(1f).scaleY(1f)
                            .translationX(circleAddButton.getPointFromAngle(180f).x).translationY(-circleAddButton.getPointFromAngle(180f).y).start();
                    addMealList[1].animate().alpha(1.0f).scaleX(1f).scaleY(1f)
                            .translationX(circleAddButton.getPointFromAngle(150f).x).translationY(-circleAddButton.getPointFromAngle(150f).y).start();
                    addMealList[2].animate().alpha(1.0f).scaleX(1f).scaleY(1f)
                            .translationX(circleAddButton.getPointFromAngle(120f).x).translationY(-circleAddButton.getPointFromAngle(120f).y).start();
                    addMealList[3].animate().alpha(1.0f).scaleX(1f).scaleY(1f)
                            .translationX(circleAddButton.getPointFromAngle(90f).x).translationY(-circleAddButton.getPointFromAngle(90f).y).start();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    float angle =
                            circleAddButton.getAngleByPoint(new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY()));
                    if (circleAddButton.getDistaceFromCenter(new Point((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) < 170)
                        return true;
                    if (angle > 165f) {
                        if (addButtonSelected == 1) return true;
                        makeSound(Sound.Sounds.valueOf("BIP_" + ((int) (Math.random() * 13) + 1)));
                        addButtonSelected = 1;
                        addMealList[0].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast_hover));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));
                    } else if (angle > 135f) {
                        if (addButtonSelected == 2) return true;
                        makeSound(Sound.Sounds.valueOf("BIP_" + ((int) (Math.random() * 13) + 1)));
                        addButtonSelected = 2;
                        addMealList[0].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch_hover));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));

                    } else if (angle > 105) {
                        if (addButtonSelected == 3) return true;
                        makeSound(Sound.Sounds.valueOf("BIP_" + ((int) (Math.random() * 13) + 1)));
                        addButtonSelected = 3;
                        addMealList[0].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack_hover));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));

                    } else if (angle > 75) {
                        if (addButtonSelected == 4) return true;
                        makeSound(Sound.Sounds.valueOf("BIP_" + ((int) (Math.random() * 13) + 1)));
                        addButtonSelected = 4;
                        addMealList[0].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner_hover));

                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    addButtonPressed = false;
                    imageViewAddButton.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
                    imageViewAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_button));
                    makeSound(Sound.Sounds.BIP_3);

                    addMealList[0].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                    addMealList[1].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                    addMealList[2].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                    addMealList[3].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();

                    addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                    addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                    addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                    addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));

                    if(addButtonSelected>0) {
                        AddFood.mealType = MealType.values()[addButtonSelected - 1];
                        Intent i = (new Intent(MainActivity.this, AddFood.class));
                        startActivityForResult(i, REQUEST_CODE_ADD);
                    }
                }
                return true;
            }
        });

        APICommunication.requestFoodList(token);

        scrollViewMenuDiary.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                System.out.println(scrollViewMenuDiary.getScrollY());
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
                        String dayId =
                                String.valueOf(dayOfMonth) + "-" +
                                        String.valueOf(monthOfYear + 1) + "-" +
                                        String.valueOf(year);
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
                            Integer.parseInt(dateForValues.split("-")[1])-1,
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadPieChartData() {
        loadTodayValues();

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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadTodayValues() {
        breakfastFoodList.removeAllViews();
        lunchFoodList.removeAllViews();
        snackFoodList.removeAllViews();
        dinnerFoodList.removeAllViews();
        carbohydrates = 0f;
        proteins = 0f;
        lipids = 0f;
        calories = 0f;

        JSONObject response = APICommunication.requestTodayValues(token);
        if (response.keySet().contains("result") && response.get("result").toString().contains("error")) {
            Toast.makeText(getApplicationContext(), response.get("result").toString(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.keySet().contains("result") && response.get("result").equals("day not found"))
            return;
        carbohydrates = ((Number) response.get("carbohydrate")).floatValue();
        proteins = ((Number) response.get("protein")).floatValue();
        lipids = ((Number) response.get("lipid")).floatValue();
        calories = ((Number) response.get("calories")).floatValue();
        water = ((Number) response.get("water")).floatValue();
        fiber = ((Number) response.get("fiber")).floatValue();
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
        textViewFiber.setText("(" + String.format("%.2f", fiber) + " gr)");
        textViewSodium.setText("(" + String.format("%.2f", sodium * 1000) + " mg)");
        textViewCalcium.setText("(" + String.format("%.2f", calcium * 1000) + " mg)");
        textViewPotassium.setText("(" + String.format("%.2f", potassium * 1000) + " mg)");
        textViewIron.setText("(" + String.format("%.2f", iron * 1000) + " mg)");
        textViewVitaminA.setText("(" + String.format("%.2f", vitaminA * 1000) + " mg)");
        textViewVitaminC.setText("(" + String.format("%.2f", vitaminC * 1000) + " mg)");

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
        entries.add(new PieEntry(1-progress, ""));

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

        Legend l = pieChartWater.getLegend();
        l.setEnabled(false);

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
        textViewEmail.setText(response.get("email").toString());
        textViewNickname.setText(response.get("nickname").toString());
        try {
            birth = new SimpleDateFormat("yyyy-MM-dd").parse(response.get("birth").toString());
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
        Object[] weights =
                ((HashMap<LocalDate, Integer>) response.get("weight")).values().toArray();
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
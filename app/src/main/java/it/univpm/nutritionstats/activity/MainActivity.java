package it.univpm.nutritionstats.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.Circle;
import it.univpm.nutritionstats.utility.InputOutputImpl;

public class MainActivity extends AppCompatActivity {
    public enum Diet {CLASSIC, PESCATARIAN, VEGETARIAN, VEGAN}

    ;

    public enum Gender {MALE, FEMALE}

    ;
    final        String TOKEN_PATH           = "token.dat";
    final static int    REQUEST_CODE_SCANNER = 1;
    final static int    REQUEST_CODE_LOGIN   = 2;

    public static ArrayList<JSONObject> resultList = new ArrayList<JSONObject>();

    public static String token         = "";
    public static String userEmail     = "";
    public static String userName      = "";
    public static Diet   diet          = null;
    public static int    weight        = 0;
    public static int    height        = 0;
    public static Gender gender        = null;
    public static int    born          = 0;
    //Current Values
    public static float  carbohydrates = 0;
    public static float  proteins      = 0;
    public static float  lipids        = 0;
    public static float  calories      = 0;
    public static float  water         = 0;

    private ConstraintLayout menuDiary                               = null;
    private ConstraintLayout menuUser                                = null;
    private ImageView        imageViewUserPhoto, imageViewUserPhoto2 = null;
    private PieChart  pieChartCPG           = null;
    private TextView  textViewCarboydrats   = null;
    private TextView  textViewProteins      = null;
    private TextView  textViewLipids        = null;
    private TextView  textViewCalories      = null;
    private ImageView imageViewAddButton    = null;
    private ImageView imageViewAddBreakfast = null;
    private ImageView imageViewAddLunch     = null;
    private ImageView imageViewAddSnack     = null;
    private ImageView imageViewAddDinner    = null;

    private ImageView imageViewDiary   = null;
    private ImageView imageViewUser    = null;
    private TextView  textViewEmail    = null;
    private TextView  textViewNickname = null;
    private TextView  textViewWeight   = null;
    private TextView  textViewHeight   = null;
    private ImageView imageViewGender  = null;
    private ImageView imageViewDiet    = null;

    private int actualTab = 0;
    private boolean addButtonPressed=false;
    Circle circleAddButton=null;
    private int addButtonSelected=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);

        menuDiary = findViewById(R.id.menuDiary);
        menuUser = findViewById(R.id.menuUser);
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto);
        imageViewUserPhoto2 = findViewById(R.id.imageViewUserPhoto2);
        pieChartCPG = findViewById(R.id.pieChartCPG);
        textViewCarboydrats = findViewById(R.id.textViewCarboydrats);
        textViewProteins = findViewById(R.id.textViewProteins);
        textViewLipids = findViewById(R.id.textViewLipids);
        textViewCalories = findViewById(R.id.textViewCalories);
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
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewHeight = findViewById(R.id.textViewHeight);
        imageViewGender = findViewById(R.id.imageViewGender);
        imageViewDiet = findViewById(R.id.imageViewDiet);

        /*buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = (new Intent(MainActivity.this, Scanner.class));
                i.putExtra("ip", API_BASE_URL);
                startActivityForResult(i, REQUEST_CODE_SCANNER);
            }
        });*/

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
                    addButtonSelected=0;
                    circleAddButton= new Circle(460,new Point((int)motionEvent.getRawX(),(int)motionEvent.getRawY()));
                    addButtonPressed=true;
                    imageViewAddButton.animate().scaleX(1.30f).scaleY(1.30f).setDuration(160).start();
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
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    float angle=circleAddButton.getAngleByPoint(new Point((int)motionEvent.getRawX(),(int)motionEvent.getRawY()));
                    if(circleAddButton.getDistaceFromCenter(new Point((int)motionEvent.getRawX(),(int)motionEvent.getRawY()))<170)
                        return true;
                    if(angle>165f){
                        if(addButtonSelected==1)return true;
                        addButtonSelected=1;
                        addMealList[0].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast_hover));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));
                    }
                    else if(angle>135f){
                        if(addButtonSelected==2)return true;
                        addButtonSelected=2;
                        addMealList[0].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch_hover));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));
                    }
                    else if(angle>105){
                        if(addButtonSelected==3)return true;
                        addButtonSelected=3;
                        addMealList[0].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[1].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[2].animate().scaleX(1.35f).scaleY(1.35f).setDuration(120).start();
                        addMealList[3].animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                        addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                        addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                        addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack_hover));
                        addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));
                    }
                    else if(angle>75){
                        if(addButtonSelected==4)return true;
                        addButtonSelected=4;
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
                    addButtonPressed=false;
                    imageViewAddButton.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
                    imageViewAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_button));

                    addMealList[0].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                    addMealList[1].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                    addMealList[2].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();
                    addMealList[3].animate().alpha(0f).scaleX(0.2f).scaleY(0.2f).translationX(0).translationY(0).start();

                    addMealList[0].setImageDrawable(getResources().getDrawable(R.drawable.breakfast));
                    addMealList[1].setImageDrawable(getResources().getDrawable(R.drawable.lunch));
                    addMealList[2].setImageDrawable(getResources().getDrawable(R.drawable.snack));
                    addMealList[3].setImageDrawable(getResources().getDrawable(R.drawable.dinner));
                    
                    Toast.makeText(getApplicationContext(),String.valueOf(addButtonSelected),Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    private void loadPieChartData() {
        loadTodayValues();

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((carbohydrates * 4f) / calories, "Carbohydrates"));
        entries.add(new PieEntry((proteins * 4f) / calories, "Proteins"));
        entries.add(new PieEntry((lipids * 9f) / calories, "Lipids"));

        /*entries.add(new PieEntry(0.33f, "Carbohydrates"));
        entries.add(new PieEntry(0.33f, "Proteins"));
        entries.add(new PieEntry(0.33f, "Lipids"));*/

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

        pieChartCPG.animateY(1000, Easing.EaseInOutElastic);

        Legend l = pieChartCPG.getLegend();
        l.setEnabled(false);
        /*l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextColor(Color.WHITE);
        l.setTypeface(Typeface.DEFAULT_BOLD);
        l.setTextSize(12);*/
    }

    private void fromUserToDiary() {
        menuUser.setVisibility(View.VISIBLE);
        menuUser.setTranslationX(0);
        menuUser.setAlpha(1);
        menuUser.animate().translationX(600).alpha(0).setDuration(401).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (animation.getDuration() == 401)
                    menuUser.setVisibility(View.GONE);
            }
        }).start();

        menuDiary.setVisibility(View.VISIBLE);
        menuDiary.setTranslationX(-600);
        menuDiary.setAlpha(0);
        menuDiary.animate().translationX(0).alpha(1).setDuration(400).start();
        actualTab = 0;
    }

    private void fromDiaryToUser() {
        menuUser.setVisibility(View.VISIBLE);
        menuUser.setTranslationX(600);
        menuUser.setAlpha(0);
        menuUser.animate().translationX(0).alpha(1).setDuration(400).start();

        menuDiary.setVisibility(View.VISIBLE);
        menuDiary.setTranslationX(0);
        menuDiary.setAlpha(1);
        menuDiary.animate().translationX(-600).alpha(0).setDuration(401).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (animation.getDuration() == 401)
                    menuDiary.setVisibility(View.GONE);
            }
        }).start();
        actualTab = 1;
    }

    private void loadTodayValues() {
        JSONObject response = APICommunication.requestTodayValues(token);
        if (response.keySet().contains("result") && response.get("result").equals("day not found"))
            return;
        carbohydrates = ((Number) response.get("carbohydrates")).floatValue();
        proteins = ((Number) response.get("proteins")).floatValue();
        lipids = ((Number) response.get("lipids")).floatValue();
        calories = ((Number) response.get("calories")).floatValue();
        water = ((Number) response.get("water")).floatValue();

        textViewCarboydrats.setText(String.valueOf(carbohydrates));
        textViewProteins.setText(String.valueOf(proteins));
        textViewLipids.setText(String.valueOf(lipids));
        textViewCalories.setText(String.valueOf(calories));
    }

    private boolean isLogged() {
        InputOutputImpl file = new InputOutputImpl(getApplicationContext(), TOKEN_PATH);
        if (!file.existFile())
            return false;
        token = file.readFile().split(":")[2];
        JSONObject response = APICommunication.requestLogin(token);
        if (response.get("result").equals("not found"))
            return false;
        else {
            login(response);
            return true;
        }
    }

    private void login(JSONObject response) {

        textViewEmail.setText(response.get("email").toString());
        textViewNickname.setText(response.get("nickname").toString());
        Object[] weights =
                ((HashMap<LocalDate, Integer>) response.get("weight")).values().toArray();
        textViewWeight.setText(weights[weights.length - 1].toString());
        Object[] heights =
                ((HashMap<LocalDate, Integer>) response.get("height")).values().toArray();
        textViewHeight.setText(heights[heights.length - 1].toString());
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
    }

    private void signUp() {
        Toast.makeText(getApplicationContext(), "Welcome, please proceed with the signup!", Toast.LENGTH_LONG).show();

        Intent i = (new Intent(MainActivity.this, Login.class));
        startActivityForResult(i, REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCANNER:
                if (!resultList.isEmpty()) {
                    //textViewOutput.setText(resultList.get(0).toJSONString());
                    resultList.remove(0);
                }
                break;
            case REQUEST_CODE_LOGIN:
                JSONObject response =
                        new APICommunication().requestSignUp(userName, userEmail, born, diet, weight, height, gender);
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
}
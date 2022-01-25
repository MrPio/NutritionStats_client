package it.univpm.nutritionstats.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.graphics.dialog.DefaultDropDownDialog;
import it.univpm.nutritionstats.utility.io.InputOutput;
import it.univpm.nutritionstats.utility.io.Serialization;
import it.univpm.nutritionstats.utility.sound.Sound;

public class AddFoodByName extends AppCompatActivity {

    public final static String SEARCH_CACHE_PATH = "foodSearchHistory.dat";
    Serialization searchCacheSerialization;

    Button   buttonAddFood         = null;
    EditText editTextFoodName      = null;
    EditText editTextPortionWeight = null;
    TextView textViewResult        = null;

    private TextView textViewCalories     = null;
    private TextView textViewCarbohydrate = null;
    private TextView textViewProteins     = null;
    private TextView textViewLipids       = null;
    private TextView textViewFiber        = null;
    private TextView textViewSodium       = null;
    private TextView textViewCalcium      = null;
    private TextView textViewPotassium    = null;
    private TextView textViewIron         = null;
    private TextView textViewVitaminA     = null;
    private TextView textViewVitaminC     = null;
    private TextView textViewWater        = null;

    Set<String>     cacheSearch        = new HashSet<>();
    HashSet<String> cacheSearchWeights = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_by_name);
        initializeViews();
        searchCacheSerialization = new Serialization(getApplicationContext(), SEARCH_CACHE_PATH);

        buttonAddFood.setOnClickListener(view -> {
            hideKeyboard();
            String foodName = editTextFoodName.getText().toString();
            if (editTextPortionWeight.getText().toString().equals("")) return;
            int weight = Integer.parseInt(editTextPortionWeight.getText().toString());

            if (((Button) view).getText().equals("CONFIRM")) {
                saveSearchCache(
                        editTextFoodName.getText().toString(),
                        editTextPortionWeight.getText().toString()
                );
                APICommunication.requestAddFoodByName(MainActivity.token, AddFood.mealType, foodName, weight);
                Sound.makeSound(getApplicationContext(), Sound.Sounds.FOOD_1);
                startActivity(getIntent());
                finish();
                Toast.makeText(getApplicationContext(),foodName+" added successfully!",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!foodName.equals("") && weight != 0) {
                JSONObject infoAll =
                        APICommunication.getInfoFromFoodName(foodName, weight, "GR");
                if (infoAll.keySet().contains("message") && infoAll.get("message").toString().contains("Sorry")) {
                    Sound.makeSound(getApplicationContext(), Sound.Sounds.NO_BUY);
                    Toast.makeText(getApplicationContext(), infoAll.get("message").toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (infoAll.keySet().contains("result") && infoAll.get("result").toString().contains("error")) {
                    Sound.makeSound(getApplicationContext(), Sound.Sounds.NO_BUY);
                    Toast.makeText(getApplicationContext(), infoAll.get("result").toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                Sound.makeSound(getApplicationContext(), Sound.Sounds.BADGE);

                JSONArray infoNutrient = (JSONArray) infoAll.get("nutrientList");
                JSONArray infoNotNutrient = (JSONArray) infoAll.get("notNutrientList");

                float carbohydrate =
                        ((Number) (((JSONObject) infoNutrient.get(0)).get("quantity"))).floatValue();
                float proteins =
                        ((Number) (((JSONObject) infoNutrient.get(1)).get("quantity"))).floatValue();
                float lipids =
                        ((Number) (((JSONObject) infoNutrient.get(2)).get("quantity"))).floatValue();

                textViewCalories.setText("(" + (carbohydrate * 4 + proteins * 4 + lipids * 9) + " kcal)");
                textViewCarbohydrate.setText("(" + ((JSONObject) infoNutrient.get(0)).get("quantity").toString() + " gr)");
                textViewProteins.setText("(" + ((JSONObject) infoNutrient.get(1)).get("quantity").toString() + " gr)");
                textViewLipids.setText("(" + ((JSONObject) infoNutrient.get(2)).get("quantity").toString() + " gr)");
                textViewFiber.setText("(" + ((Number) (((JSONObject) infoNotNutrient.get(1)).get("quantity"))).floatValue() + " gr)");
                textViewSodium.setText("(" + ((Number) (((JSONObject) infoNutrient.get(7)).get("quantity"))).floatValue() * 1000f + " mg)");
                textViewCalcium.setText("(" + ((Number) (((JSONObject) infoNutrient.get(5)).get("quantity"))).floatValue() * 1000f + " mg)");
                textViewPotassium.setText("(" + ((Number) (((JSONObject) infoNutrient.get(6)).get("quantity"))).floatValue() * 1000f + " mg)");
                textViewIron.setText("(" + ((Number) (((JSONObject) infoNutrient.get(8)).get("quantity"))).floatValue() * 1000f + " mg)");
                textViewVitaminA.setText("(" + ((Number) (((JSONObject) infoNutrient.get(3)).get("quantity"))).floatValue() * 1000f + " mg)");
                textViewVitaminC.setText("(" + ((Number) (((JSONObject) infoNutrient.get(4)).get("quantity"))).floatValue() * 1000f + " mg)");
                textViewWater.setText("(" + ((JSONObject) infoNotNutrient.get(0)).get("quantity").toString() + " g)");

                buttonAddFood.setText("CONFIRM");
            }
        });

        editTextPortionWeight.setOnKeyListener((view, i, keyEvent) -> {
            buttonAddFood.setText("Search food");
            return false;
        });
        editTextFoodName.setOnKeyListener((view, i, keyEvent) -> {
            buttonAddFood.setText("Search food");
            cacheSearchWeights.clear();
            return false;
        });

        editTextFoodName.setOnFocusChangeListener((view, b) -> {
            if (b && cacheSearch.size() > 0) {
                PopupWindow popupWindow =
                        new DefaultDropDownDialog(new ArrayList<>(cacheSearch), 500).popupWindows(AddFoodByName.this);
                ((ListView) popupWindow.getContentView()).setOnItemClickListener((adapterView, view1, i, l) -> {
                    cacheSearchWeights =
                            ((HashMap<String, HashSet<String>>) searchCacheSerialization.loadObject()).get(adapterView.getItemAtPosition(i).toString());
                    editTextFoodName.setText(adapterView.getItemAtPosition(i).toString());
                    buttonAddFood.setText("Search food");
                    popupWindow.dismiss();
                    if (editTextPortionWeight.requestFocus())
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                });
                popupWindow.showAsDropDown(editTextFoodName, 0, 0);
            }
        });
        editTextPortionWeight.setOnFocusChangeListener((view, b) -> {
            if (b)
                editTextPortionWeight.setText("");
            if (b && cacheSearchWeights.size() > 0) {
                PopupWindow popupWindow =
                        new DefaultDropDownDialog(new ArrayList<>(cacheSearchWeights), 220).popupWindows(AddFoodByName.this);
                ((ListView) popupWindow.getContentView()).setOnItemClickListener((adapterView, view1, i, l) -> {
                    editTextPortionWeight.setText(adapterView.getItemAtPosition(i).toString());
                    hideKeyboard();
                    popupWindow.dismiss();
                    buttonAddFood.performClick();
                });
                popupWindow.showAsDropDown(editTextPortionWeight, 0, 0);
                editTextPortionWeight.requestFocus();
            }
        });

        loadSearchCache();
    }

    private void saveSearchCache(String foodName, String portionWeight) {
        HashMap<String, HashSet<String>> cache = new HashMap<>();
        if (searchCacheSerialization.existFile())
            cache = (HashMap<String, HashSet<String>>) searchCacheSerialization.loadObject();
        if (cache.containsKey(foodName))
            cache.get(foodName).add(portionWeight);
        else {
            HashSet<String> list = new HashSet<>();
            list.add(portionWeight);
            cache.put(foodName, list);
        }
        searchCacheSerialization.saveObject(cache);
    }

    private void loadSearchCache() {
        if (searchCacheSerialization.existFile())
            cacheSearch =
                    ((HashMap<String, HashSet<String>>) searchCacheSerialization.loadObject()).keySet();
    }

    private void initializeViews() {
        buttonAddFood = findViewById(R.id.buttonAddFood);
        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextPortionWeight = findViewById(R.id.editTextPortionWeight);
        buttonAddFood = findViewById(R.id.buttonAddFood);

        textViewCarbohydrate = findViewById(R.id.textViewCarboydrats);
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
        textViewWater = findViewById(R.id.textViewWater);
    }

    private void hideKeyboard() {
        View currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            this.getCurrentFocus().clearFocus();
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
        }
    }

}
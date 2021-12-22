package it.univpm.nutritionstats.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.activity.MainActivity;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.Sound;

public class AddFoodByName extends AppCompatActivity {

    Button   buttonAddFood         = null;
    EditText editTextFoodName      = null;
    EditText editTextPortionWeight = null;
    TextView textViewResult        = null;

    private TextView textViewCalories    = null;
    private TextView textViewCarboydrats = null;
    private TextView textViewProteins    = null;
    private TextView textViewLipids      = null;
    private TextView textViewFiber       = null;
    private TextView textViewSodium      = null;
    private TextView textViewCalcium     = null;
    private TextView textViewPotassium   = null;
    private TextView textViewIron        = null;
    private TextView textViewVitaminA    = null;
    private TextView textViewVitaminC    = null;
    private TextView textViewWater       = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_by_name);
        buttonAddFood = findViewById(R.id.buttonAddFood);
        editTextFoodName = findViewById(R.id.editTextFoodName);
        editTextPortionWeight = findViewById(R.id.editTextPortionWeight);
        buttonAddFood = findViewById(R.id.buttonAddFood);

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
        textViewWater = findViewById(R.id.textViewWater);

        buttonAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = editTextFoodName.getText().toString();
                int weight = Integer.parseInt(editTextPortionWeight.getText().toString());

                if (((Button) view).getText().equals("CONFIRM")) {
                    APICommunication.requestAddFoodByName(MainActivity.token, AddFood.mealType, foodName, weight);
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    Sound.makeSound(getApplicationContext(),Sound.Sounds.FOOD_1);
                    finish();
                    return;
                }
                if (!foodName.equals("") && weight != 0) {
                    JSONObject infosAll =
                            APICommunication.getInfoFromFoodName(foodName, weight, "GR");
                    if (infosAll.keySet().contains("message") && infosAll.get("message").toString().contains("Sorry")) {
                        Sound.makeSound(getApplicationContext(), Sound.Sounds.NO_BUY);
                        Toast.makeText(getApplicationContext(), infosAll.get("message").toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (infosAll.keySet().contains("result") && infosAll.get("result").toString().contains("error")) {
                        Sound.makeSound(getApplicationContext(), Sound.Sounds.NO_BUY);
                        Toast.makeText(getApplicationContext(), infosAll.get("result").toString(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    Sound.makeSound(getApplicationContext(),Sound.Sounds.BADGE);

                    JSONArray infosNutrient = (JSONArray) infosAll.get("nutrientList");
                    JSONArray infosNotNutrient = (JSONArray) infosAll.get("notNutrientList");

                    float carboydrats =
                            ((Number) (((JSONObject) infosNutrient.get(0)).get("quantity"))).floatValue();
                    float proteins =
                            ((Number) (((JSONObject) infosNutrient.get(1)).get("quantity"))).floatValue();
                    float lipids =
                            ((Number) (((JSONObject) infosNutrient.get(2)).get("quantity"))).floatValue();

                    textViewCalories.setText("(" + (carboydrats * 4 + proteins * 4 + lipids * 9) + " kcal)");
                    textViewCarboydrats.setText("(" + ((JSONObject) infosNutrient.get(0)).get("quantity").toString() + " gr)");
                    textViewProteins.setText("(" + ((JSONObject) infosNutrient.get(1)).get("quantity").toString() + " gr)");
                    textViewLipids.setText("(" + ((JSONObject) infosNutrient.get(2)).get("quantity").toString() + " gr)");
                    textViewFiber.setText("(" + ((Number) (((JSONObject) infosNotNutrient.get(1)).get("quantity"))).floatValue() * 1000f + " gr)");
                    textViewSodium.setText("(" + ((Number) (((JSONObject) infosNutrient.get(7)).get("quantity"))).floatValue() * 1000f + " mg)");
                    textViewCalcium.setText("(" + ((Number) (((JSONObject) infosNutrient.get(5)).get("quantity"))).floatValue() * 1000f + " mg)");
                    textViewPotassium.setText("(" + ((Number) (((JSONObject) infosNutrient.get(6)).get("quantity"))).floatValue() * 1000f + " mg)");
                    textViewIron.setText("(" + ((Number) (((JSONObject) infosNutrient.get(8)).get("quantity"))).floatValue() * 1000f + " mg)");
                    textViewVitaminA.setText("(" + ((Number) (((JSONObject) infosNutrient.get(3)).get("quantity"))).floatValue() * 1000f + " mg)");
                    textViewVitaminC.setText("(" + ((Number) (((JSONObject) infosNutrient.get(4)).get("quantity"))).floatValue() * 1000f + " mg)");
                    textViewWater.setText("(" + ((JSONObject) infosNotNutrient.get(0)).get("quantity").toString() + " g)");

                    buttonAddFood.setText("CONFIRM");
                }
            }
        });

        editTextPortionWeight.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                buttonAddFood.setText("Search food");
                return false;
            }
        });
        editTextFoodName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                buttonAddFood.setText("Search food");
                return false;
            }
        });
    }


}
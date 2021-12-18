package it.univpm.nutritionstats.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.activity.MainActivity;
import it.univpm.nutritionstats.api.APICommunication;

public class AddFoodByName extends AppCompatActivity {

    Button buttonAddFood=null;
    EditText editTextFoodName=null;
    EditText editTextPortionWeight=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_by_name);
        buttonAddFood=findViewById(R.id.buttonAddFood);
        editTextFoodName=findViewById(R.id.editTextFoodName);
        editTextPortionWeight=findViewById(R.id.editTextPortionWeight);

        buttonAddFood=findViewById(R.id.buttonAddFood);

        buttonAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName=editTextFoodName.getText().toString();
                int weight=Integer.parseInt(editTextPortionWeight.getText().toString());
                if(!foodName.equals("") && weight!=0){
                    APICommunication.requestAddFoodByName(MainActivity.token,AddFood.mealType,foodName,weight);
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
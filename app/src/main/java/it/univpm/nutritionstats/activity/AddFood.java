package it.univpm.nutritionstats.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.DrinkType;
import it.univpm.nutritionstats.utility.Sound;

public class AddFood extends AppCompatActivity {

    public static MainActivity.MealType mealType;
    Button buttonAddByName=null;
    Button buttonAddByBarCode=null;
    Button buttonAddWater=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

        setTitle(mealType.name().replace("_"," "));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(mealType.getDrawable());
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_food);

        buttonAddByName=findViewById(R.id.buttonAddByName);
        buttonAddByBarCode=findViewById(R.id.buttonAddByBarCode);
        buttonAddWater=findViewById(R.id.buttonAddWater);

        buttonAddByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSound(Sound.Sounds.PICKUP_COIN);
                Intent i = (new Intent(AddFood.this, AddFoodByName.class));
                startActivityForResult(i, MainActivity.REQUEST_CODE_ADD_FOOD_BY_NAME);
            }
        });

        buttonAddByBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSound(Sound.Sounds.PICKUP_COIN);
                Intent i = (new Intent(AddFood.this, Scanner.class));
                startActivityForResult(i, MainActivity.REQUEST_CODE_ADD_FOOD_BY_EAN);
            }
        });

        buttonAddWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeSound(Sound.Sounds.PICKUP_COIN);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddFood.this);
                builder.setTitle("Please the amount of water you drank [mL]:");
                final Spinner input = new Spinner(getApplicationContext());
                input.setAdapter(new ArrayAdapter<>(AddFood.this, R.layout.spinner_drink_type, DrinkType.values()));
                input.setPadding(50,50,0,0);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        APICommunication.requestAddWater(MainActivity.token,(int)((DrinkType)input.getSelectedItem()).getValue());

                        Toast.makeText(AddFood.this, "Water successfully added!", Toast.LENGTH_SHORT).show();
                        makeSound(Sound.Sounds.WATER_SPLASH);

                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQUEST_CODE_ADD_FOOD_BY_EAN:
            case MainActivity.REQUEST_CODE_ADD_FOOD_BY_NAME:
                if (resultCode==RESULT_CANCELED)
                    return;

                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
    }
}
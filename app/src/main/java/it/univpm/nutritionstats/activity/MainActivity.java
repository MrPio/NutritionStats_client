package it.univpm.nutritionstats.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.InputOutputImpl;

public class MainActivity extends AppCompatActivity {
    public enum Diet{CLASSIC,PESCETARIAN,VEGETARIAN,VEGAN};
    public enum Gender{MALE,FEMALE};

    final        String TOKEN_PATH           = "token.dat";
    final        String API_BASE_URL         = "192.168.1.1:8080";
    final static int    REQUEST_CODE_SCANNER = 1;
    final static int    REQUEST_CODE_LOGIN   = 2;

    public static ArrayList<JSONObject> resultList = new ArrayList<JSONObject>();
    public static String                userEmail  = "";
    public static String                userName   = "";
    public static Diet diet=null;
    public static int weight=0;
    public static int height=0;
    public static Gender gender=null;
    public static int born=0;

    private Button   buttonScanner  = null;
    private TextView textViewOutput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScanner = findViewById(R.id.buttonScanner);
        textViewOutput = findViewById(R.id.textViewOutput);


        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = (new Intent(MainActivity.this, Scanner.class));
                i.putExtra("ip", API_BASE_URL);
                startActivityForResult(i, REQUEST_CODE_SCANNER);
            }
        });

        if (isLogged()) {
            String savedLogin = new InputOutputImpl(getApplicationContext(), TOKEN_PATH).readFile();
            userName = savedLogin.split(":")[0];
            userEmail = savedLogin.split(":")[1];
            Toast.makeText(getApplicationContext(), "Welcome back " + userName + "!", Toast.LENGTH_SHORT).show();
        } else signUp();
    }


    private boolean isLogged() {
        return new InputOutputImpl(getApplicationContext(), TOKEN_PATH).existFile();
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
                    textViewOutput.setText(resultList.get(0).toJSONString());
                    resultList.remove(0);
                }
                break;
            case REQUEST_CODE_LOGIN:
                JSONObject response = new APICommunication().requestSignUp(userName,userEmail,born,diet,weight,height,gender);
                String formatted =
                        userName + ":" + response.get("email") + ":" + response.get("token");
                new InputOutputImpl(getApplicationContext(), TOKEN_PATH).writeFile(formatted);
                Toast.makeText(getApplicationContext(), "You successfully logged-in! response=\r\n" + response, Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
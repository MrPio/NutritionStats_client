package it.univpm.nutritionstats.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;
import it.univpm.nutritionstats.utility.InputOutputImpl;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    final int TOTAL_STAGES=3;
    private GoogleApiClient googleApiClient;
    TextView  textViewLoginName   = null;
    TextView  textViewLoginEmail  = null;
    ImageView imageViewLoginPhoto = null;
    SignInButton signInButton       =null;
    Button       buttonLoginConfirm =null;
    ConstraintLayout[] menu=new ConstraintLayout[3];
    ConstraintLayout[] diet=new ConstraintLayout[4];
    EditText           editTextNumberDecimalWeight=null;
    EditText           editTextNumberDecimalHeight=null;
    ImageView male=null;
    ImageView    female           =null;
    EditText editTextDate =null;

    private static final int RC_SIGN_IN = 1;
    int stage=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textViewLoginName = findViewById(R.id.textViewLoginName);
        textViewLoginEmail = findViewById(R.id.textViewLoginEmail);
        imageViewLoginPhoto = findViewById(R.id.imageViewLoginPhoto);
        signInButton = findViewById(R.id.signInButton);
        buttonLoginConfirm=findViewById(R.id.buttonLoginNext);
        menu[0]=findViewById(R.id.LoginMenu1);
        menu[1]=findViewById(R.id.LoginMenu2);
        menu[2]=findViewById(R.id.LoginMenu3);
        diet[0]=findViewById(R.id.dietClassic);
        diet[1]=findViewById(R.id.dietPescetarian);
        diet[2]=findViewById(R.id.dietVegetarian);
        diet[3]=findViewById(R.id.dietVegan);
        male=findViewById(R.id.imageViewMale);
        female=findViewById(R.id.imageViewFemale);
        editTextNumberDecimalWeight=findViewById(R.id.editTextNumberDecimalWeight);
        editTextNumberDecimalHeight=findViewById(R.id.editTextNumberDecimalHeight);
        editTextDate=findViewById(R.id.editTextDate);


        menu[1].setVisibility(View.GONE);
        menu[2].setVisibility(View.GONE);


        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        diet[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.diet= MainActivity.Diet.CLASSIC;
                diet[0].animate().scaleX(1.2f).scaleY(1.2f).alpha(1.0f).setDuration(600).start();
                diet[1].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[2].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[3].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
            }
        });
        diet[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.diet= MainActivity.Diet.PESCATARIAN;
                diet[1].animate().scaleX(1.2f).scaleY(1.2f).alpha(1.0f).setDuration(600).start();
                diet[0].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[2].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[3].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
            }
        });
        diet[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.diet= MainActivity.Diet.VEGETARIAN;
                diet[2].animate().scaleX(1.2f).scaleY(1.2f).alpha(1.0f).setDuration(600).start();
                diet[1].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[0].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[3].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();

            }
        });
        diet[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.diet= MainActivity.Diet.VEGAN;
                diet[3].animate().scaleX(1.2f).scaleY(1.2f).alpha(1.0f).setDuration(600).start();
                diet[1].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[2].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
                diet[0].animate().scaleX(1).scaleY(1).alpha(0.5f).setDuration(600).start();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female.animate().translationY(0).scaleX(1.0f).scaleY(1.0f).setDuration(400).alpha(0.5f).start();
                male.animate().translationY(40).scaleX(1.2f).scaleY(1.2f).setDuration(600).alpha(1.0f).start();
                MainActivity.gender= MainActivity.Gender.MALE;
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female.animate().translationY(40).scaleX(1.2f).scaleY(1.2f).setDuration(400).alpha(1.0f).start();
                male.animate().translationY(0).scaleX(1.0f).scaleY(1.0f).setDuration(600).alpha(0.5f).start();
                MainActivity.gender= MainActivity.Gender.FEMALE;
            }
        });

        editTextNumberDecimalWeight.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String text=((EditText)view).getText().toString();
                if(!text.equals(""))
                    MainActivity.weight=Integer.parseInt(text);
                return false;
            }
        });
        editTextNumberDecimalHeight.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                String text=((EditText)view).getText().toString();
                if(!text.equals(""))
                    MainActivity.height=Integer.parseInt(text);
                return false;
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                MainActivity.birth= calendar.getTime();
                editTextDate.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
            }

        };
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Login.this, date, 2000, 1,1).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            gotoProfile();
        } else {
            Toast.makeText(getApplicationContext(), "Sign in cancel", Toast.LENGTH_LONG).show();
        }
    }

    private void gotoProfile() {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            showResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    showResult(googleSignInResult);
                }
            });
        }
    }

    public String generateToken(String email) {
        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = email.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bytesOfMessage);
            StringBuffer sb = new StringBuffer();
            for (byte b : theMD5digest) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            textViewLoginName.setText("Name: " + account.getDisplayName());
            MainActivity.userName=account.getDisplayName().trim().replace(' ','_');
            textViewLoginEmail.setText("Email: " + account.getEmail());
            MainActivity.userEmail=account.getEmail();

            /*
            TODO CHECK IF USER ALREADY REGISTERED
             */
            JSONObject response=APICommunication.requestLogin(generateToken(MainActivity.userEmail));
            if(response.containsKey("result") && response.get("result").equals("success!")){
                new InputOutputImpl(getApplicationContext(),"user_image").writeFile(
                        account.getPhotoUrl().toString());
                String formatted =
                        account.getDisplayName() + ":" + account.getEmail() + ":" + generateToken(MainActivity.userEmail);
                new InputOutputImpl(getApplicationContext(), MainActivity.TOKEN_PATH).writeFile(formatted);
                Picasso.get().load(account.getPhotoUrl()).into(imageViewLoginPhoto);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            try {
                new InputOutputImpl(getApplicationContext(),"user_image").writeFile(account.getPhotoUrl().toString());
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "image not found", Toast.LENGTH_LONG).show();
            }

            buttonLoginConfirm.animate().alpha(1).setDuration(1200).start();
            buttonLoginConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(stage==1)
                        slideMenu();
                    else if(stage==2){
                        if(MainActivity.diet!=null)
                            slideMenu();
                    }
                    else
                    {
                        if(MainActivity.weight!=0 && MainActivity.height!=0 && MainActivity.gender!=null && MainActivity.birth!=null) {
                            Intent resultIntent = new Intent();
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    }
                }
            });
        } else {
            returnMainActivity();
        }
    }

    private void slideMenu() {
        menu[stage-1].animate().translationX(-1000).alpha(0).setDuration(400).start();
        menu[stage].setVisibility(View.VISIBLE);
        menu[stage].setTranslationX(1000);
        menu[stage].setAlpha(0);
        menu[stage].animate().translationX(0).alpha(1).setDuration(400).start();
        stage++;
    }

    private void returnMainActivity() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
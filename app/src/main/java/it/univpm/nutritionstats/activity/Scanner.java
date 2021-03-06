package it.univpm.nutritionstats.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import it.univpm.nutritionstats.R;
import it.univpm.nutritionstats.api.APICommunication;

public class Scanner extends AppCompatActivity {
    private CodeScanner myCodeScanner;
    private CodeScannerView  scanner_view;
    private APICommunication apiCommunication;
    private boolean scanned=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scanner_view=findViewById(R.id.scanner_view);
        apiCommunication=new APICommunication();

        setupPermission();
        codeScanner();
    }

    private void codeScanner(){
        myCodeScanner = new CodeScanner(this,scanner_view);
        myCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        myCodeScanner.setFormats(CodeScanner.ONE_DIMENSIONAL_FORMATS);
        myCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        myCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        myCodeScanner.setAutoFocusEnabled(true);
        myCodeScanner.setFlashEnabled(false);
        myCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull @NotNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(scanned)return;
                        scanned=true;
                        Toast.makeText(Scanner.this, result.getText(), Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(Scanner.this);
                        builder.setTitle("Please specify portion weight:");
                        final EditText input = new EditText(getApplicationContext());
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        input.setTextColor(Color.GRAY);
                        input.setTextSize(22);
                        input.setTypeface(Typeface.SERIF);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(input.getText().length()==0)
                                    return;

                                int inputNum=Integer.parseInt(input.getText().toString());

                                JSONObject response=APICommunication.requestAddFoodByEan(MainActivity.token,AddFood.mealType,
                                        Long.parseLong(result.getText()),inputNum);
                                if(response.toJSONString().contains("Sorry, but we couldn't find"))
                                    Toast.makeText(getApplicationContext(),(String)response.get("message"),Toast.LENGTH_LONG).show();
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_CANCELED, resultIntent);
                                finish();
                            }
                        });

                        builder.show();
                    }
                });
            }
        });
        myCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull @NotNull Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Scanner.this, "Camera Initialization error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scanner_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCodeScanner.startPreview();
            }
        });
    }
    private void setupPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            makeRequest();
    }
    private void makeRequest(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if(grantResults.length==0 || grantResults[0]!=PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"You need the camera permission to use this feature!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        myCodeScanner.releaseResources();
        super.onPause();
    }
}
package it.univpm.nutritionstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

import it.univpm.nutritionstats.api.APICommunication;

public class Scanner extends AppCompatActivity {
    private CodeScanner myCodeScanner;
    private CodeScannerView  scanner_view;
    private APICommunication apiCommunication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scanner_view=findViewById(R.id.scanner_view);
        apiCommunication=new APICommunication(getIntent().getExtras().getString("ip"));

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
                        Toast.makeText(Scanner.this, result.getText(), Toast.LENGTH_SHORT).show();
                        MainActivity.resultList.add(apiCommunication.getInfoFromEan(result.getText()));
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
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
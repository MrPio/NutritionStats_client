package it.univpm.nutritionstats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public        String                url        = "";
    public static ArrayList<JSONObject> resultList = new ArrayList<JSONObject>();

    private Button   buttonScanner  = null;
    private EditText editTextIP     = null;
    private TextView textViewOutput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScanner = findViewById(R.id.buttonScanner);
        editTextIP = findViewById(R.id.editTextIP);
        textViewOutput = findViewById(R.id.textViewOutput);

        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = (new Intent(MainActivity.this, Scanner.class));
                i.putExtra("ip", url);
                startActivityForResult(i, REQUEST_CODE_SCANNER);
            }
        });
        editTextIP.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String text = ((EditText) view).getText().toString();
                    url = text;
                    Toast.makeText(getApplicationContext(), "API ip successfull set: " + text, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    public final static int REQUEST_CODE_SCANNER = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCANNER) {
            if (!resultList.isEmpty())
                textViewOutput.setText(resultList.get(0).toJSONString());
        }
    }
}
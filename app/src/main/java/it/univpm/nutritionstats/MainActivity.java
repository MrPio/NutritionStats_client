package it.univpm.nutritionstats;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonScanner= findViewById(R.id.buttonScanner);
        EditText editTextIP=findViewById(R.id.editTextIP);
        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Scanner.class));
            }
        });
        editTextIP.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction()==KeyEvent.ACTION_DOWN && keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    String text = ((EditText) view).getText().toString();
                    MainActivity.url = text;
                    Toast.makeText(getApplicationContext(),"API ip successfull set: "+text,Toast.LENGTH_SHORT);
                }
                return false;
            }
        });
    }
}
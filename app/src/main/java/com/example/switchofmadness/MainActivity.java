package com.example.switchofmadness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = this.findViewById(R.id.textView);
        SwitchOfMadness som = this.findViewById(R.id.switch_of_madness);
        som.addOnChangeListener(on -> {
            if(on) textView.setText("On");
            else textView.setText("Off");
        });
    }
}
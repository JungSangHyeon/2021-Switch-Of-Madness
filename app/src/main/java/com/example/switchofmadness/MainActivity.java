package com.example.switchofmadness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = this.findViewById(R.id.imageView);
        SwitchOfMadness som = this.findViewById(R.id.switch_of_madness);
        som.addOnChangeListener(on -> {
            if(on) imageView.setImageResource(R.drawable.thug_newton);
            else imageView.setImageResource(R.drawable.newton);
        });
    }
}
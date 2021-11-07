package com.example.switchofmadness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.findViewById(R.id.reset).setOnClickListener(v->{
            SwitchOfMadness som = this.findViewById(R.id.switch_of_madness);
            som.resetPath();
        });
        this.findViewById(R.id.reset).setOnLongClickListener(v -> {
            SwitchOfMadness som = findViewById(R.id.switch_of_madness);
            som.changeTest();
            return false;
        });
    }
}
package com.example.switchofmadness;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[][] extras = new int[][]{
                {R.id.bird, R.drawable.bird},
                {R.id.bird2, R.drawable.bird},
                {R.id.bird3, R.drawable.bird},
                {R.id.star, R.drawable.star},
                {R.id.pop1, R.drawable.pop},
                {R.id.dollar, R.drawable.dollar},
                {R.id.jake, R.drawable.jake},
        };

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int[] extra : extras){
            ImageView imageView = this.findViewById(extra[0]);
            imageView.setVisibility(View.INVISIBLE);
            Glide.with(this).asGif().load(extra[1]).into(imageView);
            imageViews.add(imageView);
        }

        ImageView newtone = this.findViewById(R.id.newtone);
        SwitchOfMadness som = this.findViewById(R.id.switch_of_madness);
        som.addOnChangeListener(on -> {
            if(on) {
                newtone.setImageResource(R.drawable.thug_newton);
                for(ImageView imageView : imageViews) imageView.setVisibility(View.VISIBLE);
                Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cantouchthis);
                MusicPlayer.setVolume(this, 0.8f);
                MusicPlayer.startMusic(this, url.toString(), true);
            }else{
                newtone.setImageResource(R.drawable.newton);
                for(ImageView imageView : imageViews) imageView.setVisibility(View.INVISIBLE);
                MusicPlayer.stopMusic();
            }
        });
    }

}
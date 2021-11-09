package com.example.switchofmadness;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer {

    private static MediaPlayer mediaPlayer;

    public static void startMusic(Context context, String musicUri, boolean loop){
        if(mediaPlayer!=null) stopMusic();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(context, Uri.parse(musicUri));
            mediaPlayer.prepare();
        } catch (IOException e) { e.printStackTrace(); }
        mediaPlayer.setLooping(loop);
        mediaPlayer.start();
    }
    public static void stopMusic() {
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void setVolume(Context context, float volumeRation) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int) (maxVolume*volumeRation);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}

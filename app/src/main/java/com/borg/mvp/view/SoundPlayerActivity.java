package com.borg.mvp.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.mvp.model.MusicUtils.Mediaplayer;
import com.borg.mvp.model.MusicUtils.SoundPlayer;

public class SoundPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_player);
        Mediaplayer.init(SoundPlayerActivity.this);
        SoundPlayer.init(SoundPlayerActivity.this);
        if (Mediaplayer.getplayflag()) {
            Mediaplayer.PlayBackgroundMusic();
        }
    }

    @Override
    protected void onDestroy() {
        Mediaplayer.ReleaseMediaplayer();
        super.onDestroy();
    }

    public void playTickSound(View view){
        SoundPlayer.playsound(R.raw.gang2);
    }
}

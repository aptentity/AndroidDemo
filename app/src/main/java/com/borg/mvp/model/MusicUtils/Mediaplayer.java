package com.borg.mvp.model.MusicUtils;

import android.content.Context;
import android.media.MediaPlayer;

import com.borg.androidemo.R;

public class Mediaplayer {

	private static Context context;
	private static MediaPlayer mediaPlayer;
	private static boolean playflag = true;

	public static void init(Context c) {
		context = c;
		mediaPlayer = MediaPlayer.create(context, R.raw.musicbackground);
		mediaPlayer.setLooping(true);
	}

	public static void PlayBackgroundMusic() {
		mediaPlayer.start();
	}

	private static void StopBackgroundMusic() {
		mediaPlayer.stop();
	}

	public static void setplayflag(boolean flag) {
		if (flag != playflag) {
			if (flag == true) {
				mediaPlayer.start();
				playflag = flag;
			} else {
				mediaPlayer.pause();  //暂停播放
				mediaPlayer.seekTo(0); //把开始播放点设置到开始断点
				playflag = flag;
			}
		}
	}
	
	public static void ReleaseMediaplayer(){
		mediaPlayer.stop();
		mediaPlayer.release();
	}

	public static boolean getplayflag() {
		return playflag;
	}

}

package com.borg.mvp.model.MusicUtils;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.borg.androidemo.R;

public class SoundPlayer {

	private static Context context;// 上下文对象
	private static boolean playflag = true;// 是否播放音效
	private static Map<Integer, Integer> soundMap; // R.raw.中的id与soundPool中的id的键值对应Map
	private static SoundPool soundPool; // 音乐池对象

	public static void init(Context c) {
		context = c;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(R.raw.gang1, soundPool.load(context, R.raw.gang1, 1));
		soundMap.put(R.raw.gang2, soundPool.load(context, R.raw.gang2, 1));
	}

	public static void playsound(int rID) {
		if (!playflag) {
			return;
		} else {
			Integer soundID = soundMap.get(rID);
			if (soundID != null) {
				soundPool.play(soundID, 1, 1, 1, 0, 1);
			}
		}
	}
	public static boolean getplayflag(){
		return playflag;
	}

	public static void setplayflag(boolean flag) {
		playflag = flag;
	}

	public static void Releasesoundplayer(){
		soundPool.release();
	}

}

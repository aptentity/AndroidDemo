package com.borg.mvp.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.borg.androidemo.R;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gulliver(feilong) on 16/2/4.
 */
public class SmileyParser {
    private static SmileyParser sInstance;

    public static SmileyParser getInstance() {
        return sInstance;
    }

    public static void init(Context context) {
        sInstance = new SmileyParser(context);
    }

    private final Context mContext;
    private final String[] mSmileyTexts;
    private final Pattern mPattern;
    private final HashMap<String, Integer> mSmileyToRes;

    private SmileyParser(Context context) {
        mContext = context;
        mSmileyTexts = mContext.getResources().getStringArray(
                DEFAULT_SMILEY_TEXTS);
        mSmileyToRes = buildSmileyToRes();
        mPattern = buildPattern();
    }

    static class Smileys {
        // 表情图片集合
        private static final int[] sIconIds = { R.drawable.earth,
                R.drawable.jupiter, R.drawable.mars,
                R.drawable.mercury, R.drawable.neptune};
        // 将图片映射为 文字
        public static int dk = 0;
        public static int dx = 1;
        public static int hx = 2;
        public static int hy = 3;
        public static int jy = 4;

        // 得到图片表情 根据id
        public static int getSmileyResource(int which) {
            return sIconIds[which];
        }
    }

    public static final int[] DEFAULT_SMILEY_RES_IDS = {
            Smileys.getSmileyResource(Smileys.dk), // 0
            Smileys.getSmileyResource(Smileys.dx), // 1
            Smileys.getSmileyResource(Smileys.hx), // 2
            Smileys.getSmileyResource(Smileys.hy), // 3
            Smileys.getSmileyResource(Smileys.jy), // 4
    };

    public static final int DEFAULT_SMILEY_TEXTS = R.array.smiley_array;
    private HashMap<String, Integer> buildSmileyToRes() {
        if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
            throw new IllegalStateException("Smiley resource ID/text mismatch");
        }
        HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(
                mSmileyTexts.length);
        for (int i = 0; i < mSmileyTexts.length; i++) {
            smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
        }
        return smileyToRes;
    }

    // 构建正则表达式
    private Pattern buildPattern() {
        StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
        patternString.append('(');
        for (String s : mSmileyTexts) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        patternString.replace(patternString.length() - 1, patternString
                .length(), ")");
        return Pattern.compile(patternString.toString());
    }

    // 根据文本替换成图片
    public CharSequence strToSmiley(CharSequence text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Matcher matcher = mPattern.matcher(text);
        while (matcher.find()) {
            int resId = mSmileyToRes.get(matcher.group());
            Drawable drawable = mContext.getResources().getDrawable(resId);
            drawable.setBounds(0, 0, 25, 25);//这里设置图片的大小
            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            builder.setSpan(imageSpan, matcher.start(),
                    matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }
}

package com.borg.mvp.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by Gulliver(feilong) on 16/3/1.
 * 图片压缩功能
 */
public class ImageResizer {
    private static final String TAG = ImageResizer.class.getSimpleName();

    public static Bitmap decodeSampledBitmapFromResource(Resources res,int resId,int reqWidth,int reqHeight){
        //先获取图片的宽高
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);

        //计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        //正式加载
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resId,options);
    }

    public static Bitmap decodeSampleBitmapFromFileDescriptor(FileDescriptor fd,int reqWidth,int reqHeight){
        //先获取图片的宽高
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        //计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        //正式加载
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        if (reqWidth==0||reqHeight==0){
            return 1;
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        LogHelper.d(TAG,"origin w="+width+" h="+height);
        int inSampleSize = 1;
        if (height>reqHeight&&width>reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSampleSize)>reqHeight&&(halfWidth/inSampleSize)>reqWidth){
                inSampleSize *=2;
            }
        }
        LogHelper.d(TAG,"sampleSize:"+inSampleSize);
        return inSampleSize;
    }
}

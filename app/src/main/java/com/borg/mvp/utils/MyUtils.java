package com.borg.mvp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Gulliver(feilong) on 16/3/1.
 */
public class MyUtils {
    public static void close(InputStream stream){
        try{
            stream.close();
        }catch (IOException e){

        }
    }

    public static void close(OutputStream stream){
        try{
            stream.close();
        }catch (IOException e){

        }
    }
}

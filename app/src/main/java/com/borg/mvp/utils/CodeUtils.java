package com.borg.mvp.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Gulliver(feilong) on 16/3/18.
 */
public class CodeUtils {
    public static String getUTF8(String source){
        try {
            return URLEncoder.encode(source, "UTF-8")
            //return URLDecoder.decode(source, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return source;
    }
}

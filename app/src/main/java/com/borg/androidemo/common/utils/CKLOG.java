package com.borg.androidemo.common.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CKLOG {
	
	//得到调用日志输出者的 文件名+类名+方法+行号
	// 但是我们yunos的虚拟机, 无法得到行号......
	public static String getCallerInfo(){
		
		StackTraceElement[]stack = new Throwable().getStackTrace() ;
		if( stack.length >=3 ){
			StackTraceElement ste = stack[2];
	        return ste.getFileName() + "##"+ste.getClassName() +"::"+ ste.getMethodName()+"("+ ste.getLineNumber()+")" ;
		}else
			return "";
    }

	private static String getCurDate( ){

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
		String strtime = sdf.format( new Date() ); //Â∞èÊó∂:ÂàÜÈíüÔø?
		return strtime;
	}

	// TODO: 15/11/13  
	public static void Debug(String tag, String msg)
	{
		Log.i("[CLOUDKIT_DEBUG]" + tag, getCurDate() + " " + msg);
	}
	
	public static void Error(String tag, String msg)
	{
		Log.e("[CLOUDKIT_DEBUG]" + tag, getCurDate() + " " + msg + " " + getCallerInfo());
	}
	
	public static void Info(String tag, String msg)
	{
		Log.i("[CLOUDKIT_DEBUG]" + tag, getCurDate() + " " + msg);
	}

	// TODO: 15/11/13
	public static void Verbose(String tag, String msg)
	{
		Log.i("[CLOUDKIT_DEBUG]" + tag, getCurDate() + " " + msg);
	}
	
	public static void Warning(String tag, String msg)
	{
		Log.w("[CLOUDKIT_DEBUG]" + tag, getCurDate() + " " + msg);
	}
	
	public static void Error(String msg)
	{
		Log.e("[CLOUDKIT_DEBUG]", getCurDate() + " " + msg + " " + getCallerInfo());
	}
	public static void Exception(String tag, Exception e)
	{
		Log.e("[CLOUDKIT_DEBUG]" + tag, getCurDate() + " " + " " + getCallerInfo() + Log.getStackTraceString(e));
	}
}

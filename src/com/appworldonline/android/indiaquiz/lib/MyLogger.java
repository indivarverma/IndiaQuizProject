package com.appworldonline.android.indiaquiz.lib;

import android.util.Log;

public class MyLogger {
	
	public static void d(String tag, String msg){
		Log.d("IndiaQuiz Logs:: "+tag, msg);
	}
	
	public static void e(String tag, String msg){
		Log.e("IndiaQuiz Logs:: "+tag, msg);
	}
	
	public static void i(String tag, String msg){
		Log.i("IndiaQuiz Logs:: "+tag, msg);
	}
	
	public static void w(String tag, String msg){
		Log.w("IndiaQuiz Logs:: "+tag, msg);
	}
	
	public static void v(String tag, String msg){
		Log.v("IndiaQuiz Logs:: "+tag, msg);
	}
	
	public static void wtf(String tag, String msg){
		Log.wtf("IndiaQuiz Logs:: "+tag, msg);
	}

}

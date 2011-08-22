package com.appworldonline.android.indiaquiz.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.Preference;
import android.text.Editable;

public class PersistenceStorage {
	
	public static final String PREFS_NAME = "LocalPreferences";
	public static final String CATEGORY_LAST_UPDATED_DATE = "CATEGORY_LAST_UPDATED_DATE";
	public static final String LAST_KIT_FOR_ONE_TIME_MESSAGE = "LAST_KIT_FOR_ONE_TIME_MESSAGE";
	private Context context;
	private SharedPreferences preference;
	private Editor editor;
	private static PersistenceStorage instance = null;
	
	public static PersistenceStorage getInstance(Context context){
		if(instance == null){
			instance = new PersistenceStorage(context);
		}
		return instance;
	}
	
	private PersistenceStorage(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = preference.edit();
	}
	
	public void setCategoryLastUpdatedDate(String _sdate){
		editor.putString(PersistenceStorage.CATEGORY_LAST_UPDATED_DATE, _sdate);
		editor.commit();
	}
	
	public String getCategoryLastUpdatedDate(){
		return preference.getString(CATEGORY_LAST_UPDATED_DATE, null);
	}
	public void setLastKitInWhichOneTimeMessageWasShown(String _kit){
		editor.putString(PersistenceStorage.LAST_KIT_FOR_ONE_TIME_MESSAGE, _kit);
		editor.commit();
	}
	public String getLastKitInWhichOneTimeMessageWasShown(){
		return preference.getString(LAST_KIT_FOR_ONE_TIME_MESSAGE, null);
	}
}

package com.appworldonline.android.indiaquiz.lib;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.appworldonline.android.indiaquiz.LoadingPage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class FavoriteDBAdapter {
	
	
	//private Vector<FavoriteDetails> favoriteList;
	//Context context;
	private static FavoriteDBAdapter instance;
	private FavoriteDBAdapter() {
		// TODO Auto-generated constructor stub
		//context = _context;
		//favoriteList = new Vector<FavoriteDetails>();
		
		
		
		
	}
	public static FavoriteDBAdapter getInstance(){
		if(instance == null)
			instance = new FavoriteDBAdapter();
		return instance;
	}

	
	
	 public static Vector<FavoriteDetails> getFavoriteListFromLocalDB(Context context){
		DBAdapter mydbAdapter = null;
		Cursor cursor = null;
		try{
			mydbAdapter = new DBAdapter(context);
			mydbAdapter.open();
			cursor = mydbAdapter.getAllFavorites();
			if(cursor!=null){
				int numberOfFavorites = cursor.getCount();
				if(numberOfFavorites == 0){ 
					return null;
				}
			}else{
				return null;
			}
			Vector<FavoriteDetails> localfavoriteslist = new Vector<FavoriteDetails>();
			cursor.moveToFirst();
			while(cursor!=null && !cursor.isAfterLast()){
				long firstColumn = cursor.getLong(0);
				String secondColumn = cursor.getString(1);
				MyLogger.d("Fetched from DB", "first: "+firstColumn+", second: "+secondColumn);
				String name = secondColumn;
				FavoriteDetails cd = new FavoriteDetails();
				cd.setRowID(firstColumn);
				cd.setName(name);
				cd.setAssociatedCategories(getAllCategoryIDsForFavorite(context, firstColumn));
				localfavoriteslist.add(cd);
				cursor.moveToNext();
			}
			return localfavoriteslist;
		}catch(Exception e){
			return (new Vector<FavoriteDetails>());
		}
		finally{
			if(mydbAdapter!=null)
				mydbAdapter.close();
			if(cursor != null)
				cursor.close();
		}
		
	}
	static long[] getAllCategoryIDsForFavorite(Context context, long _favRowID){
		long[] categories;
		DBAdapter mydbAdapter = null;
		Cursor cursor = null;
		try{
			mydbAdapter = new DBAdapter(context);
			mydbAdapter.open();
			cursor = mydbAdapter.getAllCategoriesForFavorite(_favRowID);
			categories =  new long[cursor.getCount()];
			cursor.moveToFirst();
			int index = 0;
			while(!cursor.isAfterLast()){
				long categoryRowID = cursor.getLong(1);
				categories[index] = categoryRowID;
				index++;
				cursor.moveToNext();
			}
			return categories;
		}
		catch(Exception e){
			return null;
		}finally{
			if(mydbAdapter!=null)
				mydbAdapter.close();
			if(cursor!=null){
				try{
					cursor.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	public static boolean deleteFavorite(Context context, long favRowID){
		DBAdapter mydbAdapter = null;
		Cursor cursor = null;
		boolean result = false;
		try{
			mydbAdapter = new DBAdapter(context);
			mydbAdapter.open();
			result = mydbAdapter.deleteFavorite(favRowID);
			
		}
		catch(Exception e){
			
		}finally{
			if(mydbAdapter!=null)
				mydbAdapter.close();
			if(cursor!=null){
				try{
					cursor.close();
				}catch(Exception e){
					
				}
			}
		}
		return result;
	}
	public static boolean updateFavorite(Context context, long favrowID, String favoriteName, long[] associatedactegories){
		DBAdapter mydbAdapter = null;
		Cursor cursor = null;
		boolean result = true;
		try{
			mydbAdapter = new DBAdapter(context);
			mydbAdapter.open();
			result = mydbAdapter.updateFavorite(favrowID, favoriteName);
			if(result){
				mydbAdapter.deleteAllCategoriesForFavorite(favrowID);
				result = mydbAdapter.insertAllCategoriesForFavorite(favrowID, associatedactegories);
			}
			return result;
		}
		catch(Exception e){
			return false;
		}finally{
			if(mydbAdapter!=null)
				mydbAdapter.close();
			if(cursor!=null){
				try{
					cursor.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	public static long addNewFavorite(Context context, String favoriteName, long[] associatedactegories){
		DBAdapter mydbAdapter = null;
		Cursor cursor = null;
		long rowID = -1;
		try{
			mydbAdapter = new DBAdapter(context);
			mydbAdapter.open();
			rowID = mydbAdapter.insertFavorite(favoriteName, associatedactegories);
			return rowID;
		}
		catch(Exception e){
			return -1;
		}finally{
			if(mydbAdapter!=null)
				mydbAdapter.close();
			if(cursor!=null){
				try{
					cursor.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	public static FavoriteDetails getFavoriteDetailsForRowID(Context context, long _rowID){
		DBAdapter mydbAdapter = null;
		Cursor cursor = null;
		try{
			mydbAdapter = new DBAdapter(context);
			mydbAdapter.open();
			cursor = mydbAdapter.getFavorite(_rowID);
			cursor.moveToFirst();
			FavoriteDetails fd = new FavoriteDetails();
			fd.setRowID(_rowID);
			String favname = cursor.getString(1);
			fd.setName(favname);
			fd.setAssociatedCategories(getAllCategoryIDsForFavorite(context, _rowID));
			return fd;
		}
		catch(Exception e){
			return null;
		}finally{
			if(mydbAdapter!=null)
				mydbAdapter.close();
			if(cursor!=null){
				try{
					cursor.close();
				}catch(Exception e){
					
				}
			}
		}
	}
	
}

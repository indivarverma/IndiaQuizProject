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

public class CategoryDBAdapter {
	//public String urlString = "https://sites.google.com/a/appworldonline.com/appworldonline/products/quiz-master/quizmaster_files/categories.xml";
	
//	private Vector<CategoryDetails> categoryList;
	
	
	private static CategoryDBAdapter instance;
	
	public static CategoryDBAdapter getInstance(){
		if(instance == null)
			instance = new CategoryDBAdapter();
		return instance;
	}
	private CategoryDBAdapter(){
	}
	
	/*public Vector<CategoryDetails> getCategoryList(boolean fetchfromservermandatory){
		startLoadingScreen();
		Vector<CategoryDetails> localcategorylist = getCategoryListFromLocalDB();
		if(localcategorylist == null || localcategorylist.size() == 0 || fetchfromservermandatory){
			
			try {
				fetchCategoriesFromServer();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}else{
			categoryList = localcategorylist;
			uiActionsOnFinishLoading(true);
			return categoryList;
		}
	}*/
	private static void startLoadingScreen(Context context){
    	Intent loadingIntent = new Intent(context, LoadingPage.class);
    	loadingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(loadingIntent);
    }
	private static void stopLoadingIntent(final Context context){
		Runnable closeLoadingPopUp = new Runnable() {
			
			@Override
			public void run() {
		    	Intent stopLoading = new Intent("StopLoading");
		    	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	context.sendBroadcast(stopLoading);
			}
		};
		Thread closeLoadingThread = new Thread(closeLoadingPopUp);
		closeLoadingThread.start();
    }
	
	private static void categoryRefresh(final Context context, final boolean result){
		Runnable closeLoading = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent doneIntent = new Intent("DoneCategoryLoading");
	        	doneIntent.putExtra("SUCCESSFUL", result);
	        	
	        	context.sendBroadcast(doneIntent);
			}
		};
		Thread closeLoadingThread = new Thread(closeLoading);
		closeLoadingThread.start();
	}
	private static void uiActionsOnFinishLoadingFromServer(final Context context, final boolean result){
		stopLoadingIntent(context);
		categoryRefresh(context, result);
	}
	/*public Vector<CategoryDetails> getFetchedCategories(){
		return categoryList;
	}*/
	
	public void fetchCategoriesFromServer(final Context context, final String urlString) throws ParserConfigurationException, SAXException,MalformedURLException, IOException{
		/* Create a URL we want to load some xml-data from. */
		startLoadingScreen(context);
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					URL url = new URL(urlString);
			
					/* Get a SAXParser from the SAXPArserFactory. */
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
			
					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();
					/* Create a new ContentHandler and apply it to the XML-Reader*/
					ExampleHandler myExampleHandler = new ExampleHandler(context);
					xr.setContentHandler(myExampleHandler);
			
					/* Parse the xml-data from our URL. */
					xr.parse(new InputSource(url.openStream()));
					/* Parsing has finished. */
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					MyLogger.d("Exception", e+"");
					
					uiActionsOnFinishLoadingFromServer(context, false);
			        
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		
	}
	synchronized static void compareAndUpdateDatabase(Context context, Vector<CategoryDetails> serverlist){
		DBAdapter dbAdapter;
		dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		try{
			Vector<CategoryDetails> locallist = getCategoryListFromLocalDB(context);
			for(int servercounter = 0; servercounter < serverlist.size(); servercounter++){
				CategoryDetails currentServerCategory = serverlist.get(servercounter);
				boolean presentInLocal = false; 
				for(int localcounter = 0; locallist!=null && localcounter < locallist.size(); localcounter++){
					CategoryDetails currentLocalCategory = locallist.get(localcounter);
					if(currentLocalCategory.getName().equalsIgnoreCase(currentServerCategory.getName())){
						presentInLocal = true;
						if(currentLocalCategory.isBlocked() != currentServerCategory.isBlocked()){
							dbAdapter.updateCategory(currentLocalCategory.getRowID(), currentLocalCategory.getName(), String.valueOf(currentServerCategory.isBlocked()));
						}
						dbAdapter.clearURLsForCategory(currentLocalCategory.getRowID());
						dbAdapter.insertURLsForCategory(currentLocalCategory.getRowID(), currentServerCategory.getUrls());
					}
				}
				if(!presentInLocal){
					long row_id = dbAdapter.insertCategory(currentServerCategory.getName(), currentServerCategory.isBlocked()?"true":"false");
					dbAdapter.insertURLsForCategory(row_id, currentServerCategory.getUrls());
				}
			}
			uiActionsOnFinishLoadingFromServer(context, true);
		}catch (Exception e) {
			// TODO: handle exception
		
		}
		finally{
			if(dbAdapter!=null)
			dbAdapter.close();
		}
	}
	public static Vector<String> getCategoryURLsFromLocalDB(Context context, long _category_row_id){
		 DBAdapter dbAdapter;
		dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Cursor cursor = null;
		Vector<String> urls = null;
		try{
			cursor = dbAdapter.getURLsForCategory(_category_row_id);
			if(cursor!=null){
				int numberOfUrls = cursor.getCount();
				if(numberOfUrls == 0){ 
					return null;
				}
			}else{
				return null;
			}
			urls = new Vector<String>();
			cursor.moveToFirst();
			while(cursor!=null && !cursor.isAfterLast()){
				long firstColumn = cursor.getLong(0);
				String secondColumn = cursor.getString(1);
				MyLogger.d("Fetched from DB", "first: "+firstColumn+", second: "+secondColumn);
				String url = secondColumn.trim();
				urls.add(url);
				cursor.moveToNext();
			}
		}catch(Exception e){
			return null;
		}finally{
			if(dbAdapter!=null)
			dbAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
			
		return urls;
	}
	 public static Vector<CategoryDetails> getCategoryListFromLocalDB(Context context){
		 DBAdapter dbAdapter;
		dbAdapter = new DBAdapter(context);
		dbAdapter.open();
		Vector<CategoryDetails> localcategorieslist = null;
		Cursor cursor = null;
		try{
			cursor = dbAdapter.getAllCategories();
			if(cursor!=null){
				int numberOfCategories = cursor.getCount();
				if(numberOfCategories == 0){ 
					return null;
				}
			}else{
				return null;
			}
			localcategorieslist = new Vector<CategoryDetails>();
			cursor.moveToFirst();
			while(cursor!=null && !cursor.isAfterLast()){
				long firstColumn = cursor.getLong(0);
				String secondColumn = cursor.getString(1);
				String thirdColumn = cursor.getString(2);;
				MyLogger.d("Fetched from DB", "first: "+firstColumn+", second: "+secondColumn+", third: "+thirdColumn);
				String name = secondColumn;
				boolean blocked = Boolean.parseBoolean(thirdColumn);
				MyLogger.d("Fetched from DB", "blocked is now: "+blocked);
				CategoryDetails cd = new CategoryDetails();
				cd.setRowID(firstColumn);
				cd.setName(name);
				cd.setBlocked(blocked);
				cd.setUrls(getCategoryURLsFromLocalDB(context, firstColumn));
				localcategorieslist.add(cd);
				cursor.moveToNext();
			}
		}catch(Exception e){
			return null;
		}finally{
			if(dbAdapter!=null)
			dbAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
			
		return localcategorieslist;
	}
	 public static CategoryDetails getCategoryDetails(Context context , long categoryRowID){
			DBAdapter mydbAdapter = null;
			Cursor cursor = null;
			CategoryDetails categoryDetails = null;
			try{
				mydbAdapter = new DBAdapter(context);
				mydbAdapter.open();
				cursor = mydbAdapter.getCategory(categoryRowID);
				if(cursor.getCount() != 0){
					cursor.moveToFirst();
					categoryDetails = new CategoryDetails();
					categoryDetails.setRowID(categoryRowID);
					categoryDetails.setName(cursor.getString(1));
					categoryDetails.setBlocked(Boolean.parseBoolean(cursor.getString(2)));
					categoryDetails.setUrls(getCategoryURLsFromLocalDB(context, categoryRowID));
					//categoryDetails.setUrl(cursor.getString(3));
				}
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
			return categoryDetails;
		}
	class ExampleHandler extends DefaultHandler { 
	    StringBuffer buff = null;
	    boolean buffering = false; 
	    CategoryDetails qd;
	    Vector<CategoryDetails> servercategorylist = new Vector<CategoryDetails>();
	    Context context;
	    public ExampleHandler(Context _context) {
			// TODO Auto-generated constructor stub
	    	context = _context;
		}
	    @Override
	    public void startDocument() throws SAXException {
	        // Some sort of setting up work
	    } 
	    
	    @Override
	    public void endDocument() throws SAXException {
	        // Some sort of finishing up work
	    	
	    	//dbAdapter.insertCategory(qd.getName(), qd.isBlocked()?"true":"false");
	    	compareAndUpdateDatabase(context, servercategorylist);
	    } 
	    
	    @Override
	    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
	    	buff = new StringBuffer("");
            buffering = true;
	    	if (localName.equals("CategoryItem")) {
	        	qd = new CategoryDetails();
	        }
	    	
            
	           
	    } 
	    
	    @Override
	    public void characters(char ch[], int start, int length) {
	        if(buffering) {
	            buff.append(ch, start, length);
	        }
	    } 
	    
	    @Override
	    public void endElement(String namespaceURI, String localName, String qName) 
	    throws SAXException {
	    	String content = buff.toString();
	    	buffering = false;
	    	MyLogger.d("endElement", content+"");
	    	if (localName.equals("CategoryItem")) {
	    		servercategorylist.add(qd);
	            //dbAdapter.insertCategory(qd.getName(), qd.isBlocked()?"true":"false");
	            
	            // Do something with the full text content that we've just parsed
	        }
	    	if (localName.equals("CategoryName")) {
	        	qd.setName(content);
	        }
	    	if (localName.equals("Blocked")) {
	    		MyLogger.d("CategoryList:: parser: ", "Name:: "+qd.getName()+", Blocked:: "+content);
	        	qd.setBlocked(Boolean.parseBoolean(content));
	        }
	    	if (localName.equals("url")) {
	    		qd.addUrl(content);
	        }
	    }
	    
	}
	
	
}

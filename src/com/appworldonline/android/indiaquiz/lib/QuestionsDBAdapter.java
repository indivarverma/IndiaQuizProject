package com.appworldonline.android.indiaquiz.lib;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

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
import android.os.Handler;
import android.util.Log;

public class QuestionsDBAdapter {
	//public String urlString = "https://sites.google.com/a/appworldonline.com/appworldonline/products/quiz-master/quizmaster_files/general.xml";
	//public String urlString = "http://www.rediff.com";
	//private int questionsPulled = 0;
	public static int MAX_QUESTIONS_IN_ONE_GO = 15;
	//private Vector<QuestionDetails> questionsList;
	//Context context;
	private QuestionsDBAdapter() {
		// TODO Auto-generated constructor stub
		//context = _context;
		
		
	}
	private static QuestionsDBAdapter instance = null;
	public static QuestionsDBAdapter getInstance(){
		if(instance == null)
			instance = new QuestionsDBAdapter();
		return instance;
	}
	
    public static Vector<QuestionDetails> fetchQuestionsLocally(final Context context, Vector<Integer> _category_ids, int _questionsPerCategory){
    	DBAdapter dbaAdapter = new DBAdapter(context);
		Cursor cursor = null;
		Vector<QuestionDetails> questionsList = new Vector<QuestionDetails>();
		try{
			for(int index = 0; _category_ids!=null &&  index < _category_ids.size() ; index++){
				dbaAdapter.open();
				cursor = dbaAdapter.getAllQuestionsForCategory(_category_ids.get(index), _questionsPerCategory);
				if(cursor!=null)
					cursor.moveToFirst();
				while(cursor!=null && !cursor.isAfterLast()){
					QuestionDetails qd = new QuestionDetails();
					qd.setQuestionID(cursor.getInt(0));
					qd.setQuestion(cursor.getString(1));
					qd.addAnswer(cursor.getString(2));
					qd.addAnswer(cursor.getString(3));
					qd.addAnswer(cursor.getString(4));
					qd.addAnswer(cursor.getString(5));
					qd.setRightAnswer(cursor.getInt(6));
					qd.setLink(cursor.getString(7));
					qd.setCategoryID(cursor.getInt(8));
					qd.setTotalPresentations(cursor.getInt(9));
					qd.setCorrectAttempts(cursor.getInt(10));
					qd.setBunchFileName(cursor.getString(11));
					questionsList.add(qd);
					cursor.moveToNext();
				}
				if(dbaAdapter!=null)
					dbaAdapter.close();
				if(cursor!=null)
					cursor.close();
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			if(dbaAdapter!=null)
				dbaAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
    	return questionsList;
    	
    }
    public static void incrementTotalPresentationsForQuestion(Context context, QuestionDetails _qd){
    	if(_qd == null) return;
    	DBAdapter dbaAdapter = new DBAdapter(context);
		Cursor cursor = null;
		try{
			dbaAdapter.open();
			dbaAdapter.updateQuestionTotalPresentationsCount(_qd.getQuestionID(), _qd.getTotalPresentations()+1);
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(dbaAdapter!=null)
				dbaAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
    }
    public static int getLocalQuestionCountForCategory(Context context, int _category_row_id){
    	
    	DBAdapter dbaAdapter = new DBAdapter(context);
		Cursor cursor = null;
		try{
			dbaAdapter.open();
			Vector<Integer> v = new Vector<Integer>();
			v.add(new Integer(_category_row_id));
			cursor = dbaAdapter.getAllQuestionsForCategories(v);
			if(cursor == null) return 0;
			return cursor.getCount();
		}catch (Exception e) {
			// TODO: handle exception
			return 0;
		}finally{
			if(dbaAdapter!=null)
				dbaAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
    }
    public static int getUnpresentedQuestionCountForCategory(Context context, int _category_row_id){
    	
    	DBAdapter dbaAdapter = new DBAdapter(context);
		Cursor cursor = null;
		try{
			dbaAdapter.open();
			cursor = dbaAdapter.getAllUnPresentedQuestionsForCategory(_category_row_id);
			if(cursor == null) return 0;
			return cursor.getCount();
		}catch (Exception e) {
			// TODO: handle exception
			return 0;
		}finally{
			if(dbaAdapter!=null)
				dbaAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
    }
    public Vector<String> getAllURLsFetchedForCategory(Context context, int _category_row_id){

    	DBAdapter dbaAdapter = new DBAdapter(context);
		Cursor cursor = null;
		try{
			dbaAdapter.open();
			Vector<String> v = new Vector<String>();
			
			cursor = dbaAdapter.getAllDistinctCategoriesLocallyLoaded(_category_row_id);
			if(cursor!=null)
				cursor.moveToFirst();
			while(cursor!=null && !cursor.isAfterLast()){
				v.add(cursor.getString(0));
				cursor.moveToNext();
			}
			return v;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}finally{
			if(dbaAdapter!=null)
				dbaAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
    }
    private void startLoadingScreen(final Context context){
		Handler handler = new Handler();
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent loadingIntent = new Intent(context, LoadingPage.class);
				loadingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	context.startActivity(loadingIntent);
			}
		});
    	
    }
    private Thread stopLoadingIntent(final Context context, boolean pause){
    	
	    	Thread t = new Thread(){
	    		public void run(){
	    			try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			Intent stopLoading = new Intent("StopLoading");
	    			context.sendBroadcast(stopLoading);
	    		}
	    	};
	    	t.start();
	    	return t;
    	
		

    }
    public void fetchFromServerAndSaveLocally(final Context context, final int _category_id){
    	startLoadingScreen(context);
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String urlToDownload = null;
				try{
					Vector<String> downloadedCategories = getAllURLsFetchedForCategory(context, _category_id);
					Vector<String> allCategories = CategoryDBAdapter.getCategoryURLsFromLocalDB(context, _category_id);
					
					for(int indexInAllCategories = 0 ; indexInAllCategories < allCategories.size(); indexInAllCategories++)
					{
						String urlToCheck = allCategories.get(indexInAllCategories);
						boolean urlexists = false;
						for(int indexInDownloadedCategories = 0 ; indexInDownloadedCategories < downloadedCategories.size() ; indexInDownloadedCategories++)
						{
							String urlFromDownloaded = downloadedCategories.get(indexInDownloadedCategories);
							if(urlFromDownloaded.equals(urlToCheck)){
								urlexists = true;
								break;
							}
						}
						if(!urlexists){
							urlToDownload = urlToCheck;
							break;
						}
					}
					if(urlToDownload!=null)
					{
						fetchquestionsFromServerURL(context, _category_id, urlToDownload);
					}
					else
					{
						Thread t = stopLoadingIntent(context, false);
						try {
							t.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			        	Intent doneIntent = new Intent("DoneLoading");
			        	doneIntent.putExtra("SUCCESSFUL", true);
			        	doneIntent.putExtra("category_row_id", _category_id);
			        	context.sendBroadcast(doneIntent);
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					MyLogger.d("Exception", e+",  url: "+ urlToDownload);
					Thread t = stopLoadingIntent(context, false);
					try {
						t.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        	Intent doneIntent = new Intent("DoneLoading");
		        	doneIntent.putExtra("SUCCESSFUL", false);
		        	doneIntent.putExtra("category_row_id", _category_id);
		        	context.sendBroadcast(doneIntent);
			        
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
    }
    
    
    
	
	
	private void fetchquestionsFromServerURL(final Context context, final int _category_id, final String urlString) throws ParserConfigurationException, SAXException,MalformedURLException, IOException{
		/* Create a URL we want to load some xml-data from. */
		URL url = new URL(urlString);

		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();

		/* Get the XMLReader of the SAXParser we created. */
		XMLReader xr = sp.getXMLReader();
		/* Create a new ContentHandler and apply it to the XML-Reader*/
		ExampleHandler myExampleHandler = new ExampleHandler(context, _category_id, urlString);
		xr.setContentHandler(myExampleHandler);

		/* Parse the xml-data from our URL. */
		xr.parse(new InputSource(url.openStream()));
		/* Parsing has finished. */
		
	}
	class ExampleHandler extends DefaultHandler { 
	    StringBuffer buff = null;
	    boolean buffering = false; 
	    QuestionDetails qd;
	    int category_row_id;
	    Vector<QuestionDetails> vectorofquestionsfetchedfromserver;
	    String urlItsFetchingFrom;
	    Context context;
	    public ExampleHandler(final Context context, int _category_row_id, String _urlItsFetchingFrom) {
			// TODO Auto-generated constructor stub
	    	this.context = context;
	    	category_row_id = _category_row_id;
	    	urlItsFetchingFrom = _urlItsFetchingFrom;
	    	vectorofquestionsfetchedfromserver = new Vector<QuestionDetails>();
		}
	    @Override
	    public void startDocument() throws SAXException {
	        // Some sort of setting up work
	    } 
	    
	    @Override
	    public void endDocument() throws SAXException {
	        // Some sort of finishing up work
	    } 
	    
	    @Override
	    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
	    	buff = new StringBuffer("");
            buffering = true;
	    	if (localName.equals("QuestionItem")) {
	        	qd = new QuestionDetails();
	        	qd.setBunchFileName(urlItsFetchingFrom);
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
	    	//MyLogger.d("endElement", content+"");
	    	if (localName.equals("QuestionItem")) {
	    		vectorofquestionsfetchedfromserver.add(qd);
	            
	            // Do something with the full text content that we've just parsed
	        }
	    	if (localName.equals("Question")) {
	    		MyLogger.d("Question is ", "content: "+content.trim());
	        	qd.setQuestion(content.trim());
	        }
	    	if (localName.equals("CorrectAnswer")) {
	        	qd.setRightAnswer(Integer.parseInt(content));
	        }
	    	if (localName.equals("Answer")) {
	        	qd.addAnswer(content.trim());
	        }
	    	if (localName.equals("Link")) {
	        	qd.setLink(content.trim());
	        }
	    	if (localName.equals("Questions")) {
	    		updateLocalDatabaseAndCallUIFinishActions(context, vectorofquestionsfetchedfromserver, category_row_id, urlItsFetchingFrom);
	    		
	        }
	         
            
	    }
	}
	private void updateLocalDatabaseAndCallUIFinishActions(final Context context, Vector<QuestionDetails> _vectorofquestionsfetchedfromserver, int _category_row_id, String _url_loaded){
		updateLocalDatabaseWithQuestions(context,_category_row_id, _vectorofquestionsfetchedfromserver);
		uiFinishActions(context, _category_row_id, _url_loaded);
	}
	private void updateLocalDatabaseWithQuestions(final Context context, int _category_row_id, Vector<QuestionDetails> _vectorofquestionsfetchedfromserver){
		DBAdapter dbaAdapter = new DBAdapter(context);
		Cursor cursor = null;
		try{
			dbaAdapter.open();
			for(int i = 0 ;_vectorofquestionsfetchedfromserver!=null &&  i < _vectorofquestionsfetchedfromserver.size(); i++){
				QuestionDetails qdetails = _vectorofquestionsfetchedfromserver.elementAt(i);
				dbaAdapter.insertQuestion(qdetails.getQuestion(), qdetails.getAnswers().get(0), qdetails.getAnswers().get(1), qdetails.getAnswers().get(2), qdetails.getAnswers().get(3), qdetails.getRightAnswer(), qdetails.getLink(), _category_row_id, qdetails.getBunchFileName());
			}
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(dbaAdapter!=null)
				dbaAdapter.close();
			if(cursor!=null)
				cursor.close();
		}
		
	}
	private void uiFinishActions(final Context context, final int category_row_id, final String url_loaded){
		Thread t = stopLoadingIntent(context, true);
		try {
			t.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	Intent doneIntent = new Intent("DoneLoading");
    	doneIntent.putExtra("SUCCESSFUL", true);
    	doneIntent.putExtra("category_row_id", category_row_id);
    	doneIntent.putExtra("category_url_loaded", url_loaded);
    	context.sendBroadcast(doneIntent);
	}
	
	
}

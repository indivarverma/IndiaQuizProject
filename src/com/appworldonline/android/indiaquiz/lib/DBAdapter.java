package com.appworldonline.android.indiaquiz.lib;

import java.util.ArrayList;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter 
{
    public static final String KEY_CATEGORY_ROWID = "ROWID";
    public static final String KEY_CATEGORY_NAME = "name";
    public static final String KEY_CATEGORY_LOCK = "lock";
    
    
    public static final String KEY_URL_STORAGE_ROW_ID = "ROW_ID";
    public static final String KEY_URL_STORAGE_CATEGORY_ID = "CATEGORY_ID";
    public static final String KEY_URL_STORAGE_CATEGORY_URL = "CATEGORY_URL";
    
    public static final String KEY_FAVORITE_ROWID = "ROWID";
    public static final String KEY_FAVORITE_NAME = "name";
    
    public static final String KEY_FAVORITE_CATEGORY_ROW_ID = "ROWID";
    public static final String KEY_FAVORITE_CATEGORY_FAVORITE_ROWID = "FAV_ROWID";
    public static final String KEY_FAVORITE_CATEGORY_CATEGORY_ROWID = "CAT_ROWID";
    
    private static final String KEY_QUESTION_ROWID = "ROWID";
    private static final String KEY_QUESTION_TEXT = "question_text";
    private static final String KEY_ANSWER_1 = "answer_1";
    private static final String KEY_ANSWER_2 = "answer_2";
    private static final String KEY_ANSWER_3 = "answer_3";
    private static final String KEY_ANSWER_4 = "answer_4";
    private static final String KEY_CORRECT_ANSWER = "correct_answer";
    private static final String KEY_QUESTION_URL ="moreinfo_url";
    public static final String KEY_QUESTION_CATEGORY_ROWID = "CATEGORY_ROWID";
    public static final String KEY_QUESTION_TOTAL_PRESENTATIONS = "total_attempts";
    public static final String KEY_QUESTION_CORRECT_ATTEMPTS = "correct_attempts";
    public static final String KEY_BUNCH_FILENAME ="bunch_filename";
    
    private static final String TAG = "DBAdapter";
    
    private static final String DATABASE_NAME = "indiaquiz";
    private static final String CATEGORY_DATABASE_TABLE = "categories";
    private static final String FAVORITES_DATABASE_TABLE = "favorites";
    private static final String CATEGORY_FAVORITE_TABLE = "categories_favorites";
    private static final String QUESTIONS_TABLE = "questions";
    private static final String URL_STORAGE_TABLE = "URL_STORAGE";
    
    private static final int DATABASE_VERSION = 1;

    private static final String CATEGORY_DATABASE_CREATE =
        "create table "+CATEGORY_DATABASE_TABLE+" ("+KEY_CATEGORY_ROWID+" integer primary key autoincrement, "
        +KEY_CATEGORY_NAME+" text not null, "+KEY_CATEGORY_LOCK+" text not null);";
    private static final String FAVORITE_DATABASE_CREATE =
        "create table "+FAVORITES_DATABASE_TABLE+" ("+KEY_FAVORITE_ROWID+" integer primary key autoincrement, "
        +KEY_FAVORITE_NAME+" text not null);";
    private static final String FAVORITES_CATEGORY_CREATE = 
    	"create table "+CATEGORY_FAVORITE_TABLE+" ("+KEY_FAVORITE_CATEGORY_FAVORITE_ROWID+" integer references "+FAVORITES_DATABASE_TABLE+"("+KEY_FAVORITE_ROWID+"), "
    	+ KEY_FAVORITE_CATEGORY_CATEGORY_ROWID+" integer references "+CATEGORY_DATABASE_TABLE+"("+KEY_CATEGORY_ROWID+"));";
    	
    private static final String QUESTIONS_DATABASE_CREATE =
        "create table "+QUESTIONS_TABLE+" ("
        + KEY_QUESTION_ROWID+" integer primary key autoincrement, "
        + KEY_QUESTION_TEXT+" text not null, "
        + KEY_ANSWER_1+" text not null, "
        + KEY_ANSWER_2+" text not null, "
        + KEY_ANSWER_3+" text not null, "
        + KEY_ANSWER_4+" text not null, "
        + KEY_CORRECT_ANSWER+" integer, "
        + KEY_QUESTION_URL+" text not null, "
        + KEY_QUESTION_CATEGORY_ROWID+" integer references "+CATEGORY_DATABASE_TABLE+"("+KEY_CATEGORY_ROWID+"), "
        + KEY_QUESTION_TOTAL_PRESENTATIONS+" integer, "
        + KEY_QUESTION_CORRECT_ATTEMPTS+" integer, "
        + KEY_BUNCH_FILENAME+" text not null "
		+ ");";
    private static final String URLS_STORAGE_DATABASE_CREATE =
        "create table "+URL_STORAGE_TABLE+" ("
        + KEY_URL_STORAGE_ROW_ID+" integer primary key autoincrement, "
        + KEY_URL_STORAGE_CATEGORY_ID+" integer references "+CATEGORY_DATABASE_TABLE+"("+KEY_CATEGORY_ROWID+"), "
        + KEY_URL_STORAGE_CATEGORY_URL+" text not null"
		+ ");";
        
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(CATEGORY_DATABASE_CREATE);
            db.execSQL(FAVORITE_DATABASE_CREATE);
            db.execSQL(FAVORITES_CATEGORY_CREATE);
            db.execSQL(QUESTIONS_DATABASE_CREATE);
            db.execSQL(URLS_STORAGE_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+CATEGORY_FAVORITE_TABLE);        
            db.execSQL("DROP TABLE IF EXISTS "+CATEGORY_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+FAVORITES_DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+QUESTIONS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+URL_STORAGE_TABLE);
            onCreate(db);
        }
    }    
    
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert a title into the database---
    public long insertCategory(String name, String lock) 
    {
    	MyLogger.d("DBAdapter", "About to insert values : "+name+", "+lock);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CATEGORY_NAME, name);
        initialValues.put(KEY_CATEGORY_LOCK, lock);
        return db.insert(CATEGORY_DATABASE_TABLE, null, initialValues);
    }
    
    public void insertURLsForCategory(long _category_row_id, Vector<String> _urls){
    	if(_urls == null || _urls.size() == 0) return;
    	for(int i = 0 ; i < _urls.size() ; i++){
    		insertURLForCategory(_category_row_id, _urls.get(i));
    	}
    }
    
    public long insertURLForCategory(long _category_row_id, String _url) 
    {
    	MyLogger.d("DBAdapter", "About to insert values : "+_category_row_id+", "+_url);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_URL_STORAGE_CATEGORY_ID, _category_row_id);
        initialValues.put(KEY_URL_STORAGE_CATEGORY_URL, _url);
        return db.insert(URL_STORAGE_TABLE, null, initialValues);
    }
    
    public Cursor getURLsForCategory(long _category_row_id){
    	String whereclause = null;
    	whereclause=KEY_URL_STORAGE_CATEGORY_ID+"="+_category_row_id;
        return db.query(URL_STORAGE_TABLE, new String[] {
        		KEY_URL_STORAGE_ROW_ID,
        		KEY_URL_STORAGE_CATEGORY_URL
        		}, 
                whereclause, 
                null, 
                null, 
                null, 
                null);
    }
    
    public boolean clearURLsForCategory(long _category_row_id) 
    {
    	return db.delete(URL_STORAGE_TABLE, KEY_URL_STORAGE_CATEGORY_ID + 
        		"=" + _category_row_id, null) > 0;
    }
    
    public long insertQuestion(String _question_text, String _answer1, String _answer2, String _answer3, String _answer4, int _correct_answer, String _url, int _category_row_id, String _bunchfilename) 
    {
    	MyLogger.d("DBAdapter", "About to insert values : "+_question_text);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QUESTION_TEXT, _question_text);
        initialValues.put(KEY_ANSWER_1, _answer1);
        initialValues.put(KEY_ANSWER_2, _answer2);
        initialValues.put(KEY_ANSWER_3, _answer3);
        initialValues.put(KEY_ANSWER_4, _answer4);
        initialValues.put(KEY_CORRECT_ANSWER, _correct_answer);
        initialValues.put(KEY_QUESTION_URL, _url);
        initialValues.put(KEY_QUESTION_CATEGORY_ROWID, _category_row_id);
        initialValues.put(KEY_BUNCH_FILENAME, _bunchfilename);
        initialValues.put(KEY_QUESTION_TOTAL_PRESENTATIONS, 0);
        initialValues.put(KEY_QUESTION_CORRECT_ATTEMPTS, 0);
        return db.insert(QUESTIONS_TABLE, null, initialValues);
    }
    public boolean updateQuestionTotalPresentationsCount(long rowId, int count) 
    {
	    ContentValues args = new ContentValues();
	    args.put(KEY_QUESTION_TOTAL_PRESENTATIONS, count);
	    return db.update(QUESTIONS_TABLE, args, 
                 KEY_QUESTION_ROWID + "=" + rowId, null) > 0;
    }
    public boolean updateQuestionCorrectlyAttemptedCount(long rowId, int count) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_QUESTION_CORRECT_ATTEMPTS, count);
        return db.update(QUESTIONS_TABLE, args, 
        		KEY_QUESTION_ROWID + "=" + rowId, null) > 0;
    }
    public boolean deleteQuestion(long rowId) 
    {
        return db.delete(QUESTIONS_TABLE, KEY_QUESTION_ROWID + 
        		"=" + rowId, null) > 0;
    }
    public boolean deleteQuestionsForCategoryID(int rowId) 
    {
        return db.delete(QUESTIONS_TABLE, KEY_QUESTION_CATEGORY_ROWID + 
        		"=" + rowId, null) > 0;
    }
    public Cursor getAllUnPresentedQuestionsForCategory(int _category_row_id){
    	String whereclause = null;
    	whereclause=KEY_QUESTION_CATEGORY_ROWID+"="+_category_row_id
    				+ " and "
    				+ KEY_QUESTION_TOTAL_PRESENTATIONS +"<=0";
    				
        return db.query(QUESTIONS_TABLE, new String[] {
        		KEY_QUESTION_ROWID, 
        		KEY_QUESTION_TEXT,
        		KEY_ANSWER_1,
        		KEY_ANSWER_2,
        		KEY_ANSWER_3,
        		KEY_ANSWER_4,
        		KEY_CORRECT_ANSWER,
        		KEY_QUESTION_URL,
        		KEY_QUESTION_CATEGORY_ROWID,
        		KEY_QUESTION_TOTAL_PRESENTATIONS,
        		KEY_QUESTION_CORRECT_ATTEMPTS,
        		KEY_BUNCH_FILENAME
        		}, 
                whereclause, 
                null, 
                null, 
                null, 
                null);
    }
    public Cursor getAllQuestionsForCategory(int _category_row_id, int _numberOfQuestions) 
    {
    	String whereclause = null;
    	whereclause=KEY_QUESTION_CATEGORY_ROWID+"="+_category_row_id;
    	
        return db.query(QUESTIONS_TABLE, new String[] {
        		KEY_QUESTION_ROWID, 
        		KEY_QUESTION_TEXT,
        		KEY_ANSWER_1,
        		KEY_ANSWER_2,
        		KEY_ANSWER_3,
        		KEY_ANSWER_4,
        		KEY_CORRECT_ANSWER,
        		KEY_QUESTION_URL,
        		KEY_QUESTION_CATEGORY_ROWID,
        		KEY_QUESTION_TOTAL_PRESENTATIONS,
        		KEY_QUESTION_CORRECT_ATTEMPTS,
        		KEY_BUNCH_FILENAME
        		}, 
                whereclause, 
                null, 
                null, 
                null, 
                KEY_QUESTION_TOTAL_PRESENTATIONS,
                "0,"+_numberOfQuestions);
    }
    public Cursor getAllQuestionsForCategories(Vector<Integer> _category_row_ids) 
    {
    	String whereclause = null;
    	for(int i = 0; _category_row_ids!=null && (i < _category_row_ids.size()); i++){
    		if(whereclause==null){
    			whereclause=KEY_QUESTION_CATEGORY_ROWID+"="+_category_row_ids.get(i);
    		}else{
    			whereclause=whereclause.concat(" OR "+KEY_QUESTION_CATEGORY_ROWID+"="+_category_row_ids.get(i));
    		}
    	}
        return db.query(QUESTIONS_TABLE, new String[] {
        		KEY_QUESTION_ROWID, 
        		KEY_QUESTION_TEXT,
        		KEY_ANSWER_1,
        		KEY_ANSWER_2,
        		KEY_ANSWER_3,
        		KEY_ANSWER_4,
        		KEY_CORRECT_ANSWER,
        		KEY_QUESTION_URL,
        		KEY_QUESTION_CATEGORY_ROWID,
        		KEY_QUESTION_TOTAL_PRESENTATIONS,
        		KEY_QUESTION_CORRECT_ATTEMPTS,
        		KEY_BUNCH_FILENAME
        		}, 
                whereclause, 
                null, 
                null, 
                null, 
                KEY_QUESTION_TOTAL_PRESENTATIONS);
    }
    
    public Cursor getAllDistinctCategoriesLocallyLoaded(int _category_row_id){
    	return db.query(true, QUESTIONS_TABLE, new String[] {
        		
        		KEY_BUNCH_FILENAME
        		}, 
        		KEY_QUESTION_CATEGORY_ROWID +"="+_category_row_id, 
                null, 
                null, 
                null,
                null,
                null);
    }

    //---deletes a particular title---
    public boolean deleteCategory(long rowId) 
    {
        return db.delete(CATEGORY_DATABASE_TABLE, KEY_CATEGORY_ROWID + 
        		"=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllCategories() 
    {
        return db.query(CATEGORY_DATABASE_TABLE, new String[] {
        		KEY_CATEGORY_ROWID, 
        		KEY_CATEGORY_NAME,
        		KEY_CATEGORY_LOCK}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }

    //---retrieves a particular title---
    public Cursor getCategory(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, CATEGORY_DATABASE_TABLE, new String[] {
                		KEY_CATEGORY_ROWID,
                		KEY_CATEGORY_NAME, 
                		KEY_CATEGORY_LOCK
                		}, 
                		KEY_CATEGORY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateCategory(long rowId, String name, 
    String lock) 
    {
    	Log.d("About to update category:",name+", rowID: "+rowId);
        ContentValues args = new ContentValues();
        args.put(KEY_CATEGORY_NAME, name);
        args.put(KEY_CATEGORY_LOCK, lock);
        return db.update(CATEGORY_DATABASE_TABLE, args, 
                         KEY_CATEGORY_ROWID + "=" + rowId, null) > 0;
    }
    
    public long insertFavorite(String name, long[] categories) 
    {
    	MyLogger.d("DBAdapter", "About to insert values : "+name+", "+categories);
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(KEY_FAVORITE_NAME, name);
        long rowID = db.insert(FAVORITES_DATABASE_TABLE, null, initialValues);
        insertAllCategoriesForFavorite(rowID, categories);
    	return rowID;
        
    }
    public boolean insertAllCategoriesForFavorite(long favrowID, long[] categories){
    	boolean result = true;
    	try{
	    	for(int i = 0; i < categories.length; i++){
	    		ContentValues individualInitialValues = new ContentValues();
	    		individualInitialValues.put(KEY_FAVORITE_CATEGORY_FAVORITE_ROWID, favrowID);
	    		individualInitialValues.put(KEY_FAVORITE_CATEGORY_CATEGORY_ROWID, categories[i]); 
	    		long value = db.insert(CATEGORY_FAVORITE_TABLE, null, individualInitialValues);
	    	}
    	}catch (Exception e) {
			// TODO: handle exception
    		result = false;
		}
    	return result;
    }
    public Cursor getAllCategoriesForFavorite(long _rowID){
    	return db.query(CATEGORY_FAVORITE_TABLE, new String[] {
        		KEY_FAVORITE_CATEGORY_FAVORITE_ROWID, 
        		KEY_FAVORITE_CATEGORY_CATEGORY_ROWID}, 
        		KEY_FAVORITE_CATEGORY_FAVORITE_ROWID + "=" + _rowID,
        		null,
        		null, 
        		null, 
        		null, 
        		null);
    }
  //---deletes a particular title---
    public boolean deleteFavoriteCategory(long favoriterowId, int categoryrowid) 
    {
        return db.delete(CATEGORY_FAVORITE_TABLE, KEY_FAVORITE_CATEGORY_FAVORITE_ROWID + 
        		"=" + favoriterowId + " && " + KEY_FAVORITE_CATEGORY_CATEGORY_ROWID + "=" + categoryrowid , null) > 0;
    }
    
    public boolean deleteAllCategoriesForFavorite(long favoriterowId){
    	return db.delete(CATEGORY_FAVORITE_TABLE, KEY_FAVORITE_CATEGORY_FAVORITE_ROWID + 
        		"=" + favoriterowId  , null) > 0;
    }
    
    //---deletes a particular title---
    public boolean deleteFavorite(long favoriterowId) 
    {
    	deleteAllCategoriesForFavorite(favoriterowId);
        return db.delete(FAVORITES_DATABASE_TABLE, KEY_FAVORITE_ROWID + 
        		"=" + favoriterowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllFavorites() 
    {
        return db.query(FAVORITES_DATABASE_TABLE, new String[] {
        		KEY_FAVORITE_ROWID, 
        		KEY_FAVORITE_NAME}, 
                null, 
                null, 
                null, 
                null, 
                null);
    }

    //---retrieves a particular title---
    public Cursor getFavorite(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, FAVORITES_DATABASE_TABLE, new String[] {
                		KEY_FAVORITE_ROWID,
                		KEY_FAVORITE_NAME
                		}, 
                		KEY_FAVORITE_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateFavorite(long rowId, String name) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORITE_NAME, name);
        return db.update(FAVORITES_DATABASE_TABLE, args, 
                         KEY_FAVORITE_ROWID + "=" + rowId, null) > 0;
    }
}

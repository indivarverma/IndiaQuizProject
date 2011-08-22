package com.appworldonline.android.indiaquiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import com.appworldonline.android.indiaquiz.lib.CategoryDetails;
import com.appworldonline.android.indiaquiz.lib.CategoryDBAdapter;
import com.appworldonline.android.indiaquiz.lib.MyLogger;
import com.appworldonline.android.indiaquiz.lib.QuestionDetails;
import com.appworldonline.android.indiaquiz.lib.QuestionsDBAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class QuestionTemplate extends Activity implements OnClickListener, OnItemClickListener {
    /** Called when the activity is first created. */
	TextView questionText, questionCount, score, totalquestioncount, titleView, wronganswers;
	Button confirmAnswer, getMoreInfo, gotoNext, finishButton;
	ListView answers;
	QuestionDetails currentQuestion;
	//QuestionsDBAdapter questionsDBAdapter;
	int selectedAnswer = 0;
	int correctAnswers = 0;
	int numberOfQuestionsPulled = 0;
	//boolean locked = false;
	boolean confirmed = false;
	private DoneBroadcastReceiver dbr;
	MyBaseAdapter myBaseAdapter;
	String title;
	Vector<QuestionDetails> listOfQuestionsLeft;
	//Vector<QuestionDetails> listOfQuestionsAttempted;
	Vector<Integer> allcategoriesToFetch;
	Vector<Integer> missingCategories;
	//ArrayList<String> urlsToFetchFrom;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questiontemplate);
        //listOfQuestionsAttempted = new Vector<QuestionDetails>();
        try{
        	title = getIntent().getExtras().getString("Title");
        	//urlsToFetchFrom = getIntent().getExtras().getStringArrayList("urls");
        	ArrayList<Integer> alAllcategoriesToFetch = getIntent().getExtras().getIntegerArrayList("category_ids");
        	if(alAllcategoriesToFetch!=null){
        		allcategoriesToFetch = new Vector<Integer>();
        		for(int is = 0; is < alAllcategoriesToFetch.size(); is++){
        			Log.d("+++++++++++++++","CategoryID: <"+alAllcategoriesToFetch.get(is)+">");
        			allcategoriesToFetch.add(alAllcategoriesToFetch.get(is));
        		}
        	}
        	//Log.d("urlsToFetchFrom are here: ", "size"+urlsToFetchFrom.size());
        }catch(NullPointerException npe){
        	
        }
        if(title!=null){
        	title = title.trim();
        	title = title.concat(" Quiz");
        }
        dbr = new DoneBroadcastReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("DoneLoading");
        registerReceiver(dbr, ifilter);
        titleView = (TextView)findViewById(R.id.categoryname);
        titleView.setText(title);
        questionText=(TextView)findViewById(R.id.questionText);
        questionCount=(TextView)findViewById(R.id.questionscovered);
        score = (TextView)findViewById(R.id.correctanswers);
        wronganswers = (TextView)findViewById(R.id.wronganswers);
        totalquestioncount = (TextView)findViewById(R.id.totalquestions);
        totalquestioncount.setText(String.valueOf(QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO));
        answers = (ListView)findViewById(R.id.answers);
        answers.setOnItemClickListener(this);
        myBaseAdapter = new MyBaseAdapter();
        answers.setAdapter(myBaseAdapter);
        confirmAnswer = (Button)findViewById(R.id.confirmButton);
        confirmAnswer.setOnClickListener(this);
        
        gotoNext = (Button)findViewById(R.id.gotoNext);
        gotoNext.setOnClickListener(this);
        getMoreInfo = (Button)findViewById(R.id.getMoreInfo);
        getMoreInfo.setOnClickListener(this);
        finishButton = (Button)findViewById(R.id.finish);
        finishButton.setOnClickListener(this);
        //gotoNextQuestion();
        
        //questionsDBAdapter = new QuestionsDBAdapter(getApplicationContext());
        //indivar uncomment following line
        missingCategories = findCategoriesWithNotEnoughFreshQuestionsInDatabase(allcategoriesToFetch, (int)Math.ceil((float)QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO/allcategoriesToFetch.size()));
        if(missingCategories==null || missingCategories.size()==0){
        	//pick from local database
        	listOfQuestionsLeft = QuestionsDBAdapter.fetchQuestionsLocally(getApplicationContext(), allcategoriesToFetch, (int)Math.ceil((float)QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO/allcategoriesToFetch.size()));
        	gotoNextQuestion();
        }else{
        	//hit the server one by one
        	if(missingCategories!=null && missingCategories.size()>0){
	        	Integer category_id = missingCategories.remove(0);
	        	CategoryDetails cds = CategoryDBAdapter.getCategoryDetails(getApplicationContext(), category_id);
	        	
        		QuestionsDBAdapter.getInstance().fetchFromServerAndSaveLocally(getApplicationContext(), category_id.intValue());
	        	
        	}
        	
        }
        
        
    }
    
    private Vector<Integer> findCategoriesWithNotEnoughFreshQuestionsInDatabase(Vector<Integer> _categoryIDs, int minimumQuestions){
    	Vector<Integer> missingCategoryIDs = new Vector<Integer>();
    	for(int i = 0 ; i < _categoryIDs.size(); i++){
    		int freshQuestionsCount = QuestionsDBAdapter.getInstance().getUnpresentedQuestionCountForCategory(getApplicationContext(), _categoryIDs.get(i).intValue());
    		if(freshQuestionsCount<minimumQuestions){
    			Vector<String> completeUrls = CategoryDBAdapter.getCategoryURLsFromLocalDB(getApplicationContext(), _categoryIDs.get(i));
    			Vector<String> loadedUrls = QuestionsDBAdapter.getInstance().getAllURLsFetchedForCategory(getApplicationContext(), _categoryIDs.get(i));
    			if(loadedUrls == null || loadedUrls.size() == 0){
    				missingCategoryIDs.add(_categoryIDs.get(i));
    			}else{
    				boolean allServersThere = true;
    				for(int index = 0 ; completeUrls!=null && index < completeUrls.size(); index++){
    					String serversUrl = completeUrls.get(index);
    					boolean exists = false;
    					for(int localIndex = 0 ; localIndex < loadedUrls.size() ; localIndex++){
    						String localUrl = loadedUrls.get(localIndex);
    						if(serversUrl.equals(localUrl)){
    							exists = true;
    							break;
    						}
    					}
    					if(!exists){
    						allServersThere = false;
    						break;
    					}
    				}
    				if(!allServersThere){
    					missingCategoryIDs.add(_categoryIDs.get(i));
    				}
    			}
    			
    		}
    	}
    	return missingCategoryIDs;
    }
    private QuestionDetails pullAQuestionFromList(){
    	try{
	    	currentQuestion = listOfQuestionsLeft.remove(0);
	    	//listOfQuestionsAttempted.add(currentQuestion);
	    	numberOfQuestionsPulled++;
	    	QuestionsDBAdapter.incrementTotalPresentationsForQuestion(getApplicationContext(), currentQuestion);
	    	return currentQuestion;
    	}catch (Exception e) {
			// TODO: handle exception
    		return null;
		}
    }
    private void gotoNextQuestion(){
    	
    	confirmed = false;
    	selectedAnswer = 0;
    	
		getMoreInfo.setVisibility(View.GONE);
		gotoNext.setVisibility(View.GONE);
		confirmAnswer.setVisibility(View.VISIBLE);
		
		
    	if(numberOfQuestionsPulled>=(QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO)) {
    		finish();
    		return;
    	}
    	finishButton.setVisibility(View.GONE);
    	currentQuestion = pullAQuestionFromList();
    	if(currentQuestion == null){
    		finish(); 
    		return;
    	}
    	questionText.setText(currentQuestion.getQuestion().trim());
    	List<String> answers = currentQuestion.getAnswers();
    	myBaseAdapter.setAnswers(answers);
    	
    	questionCount.setText(String.valueOf(numberOfQuestionsPulled));
    	
    }
    @Override
    public void finish() {
    	// TODO Auto-generated method stub
    	super.finish();
    	unregisterReceiver(dbr);
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.gotoNext:
			gotoNextQuestion();
			break;
		case R.id.confirmButton:
			if(selectedAnswer<1){
				Toast.makeText(getApplicationContext(), "Please select an answer first!", 1000).show();
				break;
			}
			
			confirmed = true;
			QuestionsLists.getInstance().addAttemptedQuestion(currentQuestion, new Integer(selectedAnswer));
			if(currentQuestion.getRightAnswer() == selectedAnswer){
				//Toast.makeText(getApplicationContext(), "You are correct!", 1000).show();
				correctAnswers++;
				score.setText(String.valueOf(correctAnswers));
				
			}else{
				wronganswers.setText(String.valueOf(numberOfQuestionsPulled-correctAnswers));
				//Toast.makeText(getApplicationContext(), "Wrong Answer!", 1000).show();
			}
			
			
			confirmAnswer.setVisibility(View.GONE);
			getMoreInfo.setVisibility(View.VISIBLE);
			
			if(numberOfQuestionsPulled>=(QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO)) {
	    		finishButton.setVisibility(View.VISIBLE);
	    	}else{
	    		gotoNext.setVisibility(View.VISIBLE);
	    	}
			myBaseAdapter.notifyDataSetChanged();
			break;
		case R.id.finish:
			finish();
			Intent lastScreen = new Intent(getApplicationContext(), ResultsScreen.class);
			lastScreen.putExtra("SCORE", correctAnswers);
			lastScreen.putExtra("QUIZNAME", title);
			lastScreen.putExtra("TOTALQUESTIONSATTEMPTED", numberOfQuestionsPulled);
			startActivity(lastScreen);
			break;
		case R.id.getMoreInfo:
			Intent myIntent = new Intent(getApplicationContext(), InfoScreen.class);
			myIntent.putExtra("LINK", currentQuestion.getLink());
			startActivity(myIntent);
			break;
		}
		
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent(getApplicationContext(), ConfirmationPopup.class);
			intent.putExtra("statement", "This will stop the quiz.\n\nDo you want to return to Main Menu?");
			startActivityForResult(intent, 402);
			return true;
		default:
			return false;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 402:
			switch(resultCode){
			case -1:
				break;
			case 1:
				finish();
				break;
			}
			break;
		case 502:
			finish();
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(!confirmed){
			selectedAnswer = arg2+1;
			myBaseAdapter.notifyDataSetChanged();
		}
	}
	class MyBaseAdapter extends BaseAdapter{
		List<String> baseanswers;
		public MyBaseAdapter() {
			// TODO Auto-generated constructor stub
			baseanswers = new Vector<String>();
		}

		public void setAnswers(List<String> _answers){
			baseanswers = _answers;
			notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return baseanswers.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return baseanswers.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(arg1==null){
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				arg1 = inflater.inflate(R.layout.answerchoice, null);
			}
			RelativeLayout answerchoice = (RelativeLayout)arg1;
			TextView tv = (TextView)answerchoice.findViewById(R.id.answertext);
			ImageView answerIcon = (ImageView)answerchoice.findViewById(R.id.answericon);
			ImageView backgroundimage = (ImageView)answerchoice.findViewById(R.id.backgroundimage);
			tv.setText(baseanswers.get(arg0));
			if(confirmed){
				//MyLogger.d("Its locked", "index is "+(arg0+1)+": right answer: "+currentQuestion.getRightAnswer());
				if(arg0+1 == currentQuestion.getRightAnswer()){
					tv.setTextColor(0xFFA4CF69);
					answerIcon.setImageResource(R.drawable.right);
					backgroundimage.setImageResource(R.drawable.rightanswerbar);
				}else{
					if(arg0+1 == selectedAnswer){
						tv.setTextColor(0xFFC7C7C7);
						answerIcon.setImageResource(R.drawable.wrong);
						backgroundimage.setImageResource(R.drawable.wronganswerbar);
					}
					else{
						tv.setTextColor(0xFF000000);
						answerIcon.setImageBitmap(null);
						backgroundimage.setImageResource(R.drawable.questionbar);
					}
					
				}
			}else{
				tv.setTextColor(0xFF000000);
				if(arg0+1 == selectedAnswer){
					answerIcon.setImageBitmap(null);
					backgroundimage.setImageResource(R.drawable.selectionbar);
				}else{
					answerIcon.setImageBitmap(null);
					backgroundimage.setImageResource(R.drawable.questionbar);
				}
			}
			return arg1;
		}
		
	}
	class DoneBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, final Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("DoneLoading")){
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						if(intent.getExtras().getBoolean("SUCCESSFUL")){
							if(missingCategories!=null && missingCategories.size()>0){
					        	Integer category_id = missingCategories.remove(0);
					        	CategoryDetails cds = CategoryDBAdapter.getCategoryDetails(getApplicationContext(), category_id);
					        	QuestionsDBAdapter.getInstance().fetchFromServerAndSaveLocally(getApplicationContext(), category_id.intValue());
					        }else{
					        	//gotoNextQuestion();
					        	listOfQuestionsLeft = QuestionsDBAdapter.fetchQuestionsLocally(getApplicationContext(), allcategoriesToFetch, (int)Math.ceil((float)QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO/allcategoriesToFetch.size()));
					        	gotoNextQuestion();
					        }
						}else{
							listOfQuestionsLeft = QuestionsDBAdapter.fetchQuestionsLocally(getApplicationContext(), allcategoriesToFetch, (int)Math.ceil((float)QuestionsDBAdapter.MAX_QUESTIONS_IN_ONE_GO/allcategoriesToFetch.size()));
							if(listOfQuestionsLeft == null || listOfQuestionsLeft.size() == 0){
								Intent networkIssue = new Intent(getApplicationContext(), CustomPopup.class);
								networkIssue.putExtra("statement", "There was a problem fetching questions from the server.\n\nPlease check your internet connectivity and try again.");
								startActivityForResult(networkIssue, 502);
							}else{
								gotoNextQuestion();
							}
						}
					}
				});
				
			}
		}
		
	}
}